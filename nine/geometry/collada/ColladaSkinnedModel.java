package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;

import nine.buffer.ArrayBuffer;
import nine.buffer.Buffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.Flow;
import nine.collection.Mapping;
import nine.collection.RangeFlow;
import nine.geometry.ShadedSkinnedModel;
import nine.geometry.Skeleton;
import nine.geometry.SkinnedModelAsset;
import nine.io.Storage;
import nine.math.Matrix4f;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;

public class ColladaSkinnedModel implements SkinnedModelAsset
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaAnimationParser animationParser;
    ColladaMaterialParser materialParser;

    public ColladaSkinnedModel(
        ColladaNode node,
        ColladaGeometryParser geometryParser,
        ColladaSkinParser skinParser,
        ColladaAnimationParser animationParser,
        ColladaMaterialParser materialParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.animationParser = animationParser;
        this.materialParser = materialParser;
    }
    public ColladaSkinnedModel(ColladaNode node)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            new ColladaBasicAnimationParser(),
            new ColladaBasicMaterialParser());
    }
    public ColladaSkinnedModel(ColladaNode node, ColladaAnimationParser animationParser)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            animationParser,
            new ColladaBasicMaterialParser());
    }

    interface RawMeshAction
    {
        void call(DrawingAttributeBuffer buffer, Buffer<Integer> rawIndices);
    }
    interface RawMesh
    {
        void accept(RawMeshAction action);

        static RawMesh of(DrawingAttributeBuffer buffer, Buffer<Integer> rawIndices)
        {
            return action -> action.call(buffer, rawIndices);
        }
        static RawMesh many(RawMesh...meshes)
        {
        	return a ->
        	{
        		for(RawMesh m : meshes) m.accept(a);
        	};
        }
    }

    @Override
    public ShadedSkinnedModel load(OpenGL gl, Storage storage)
    {
        HashMap<String, RawMesh> meshes = new HashMap<>();
        HashMap<String, RawMesh> skinnedMeshes = new HashMap<>();

        HashMap<String, Skeleton> invBindPoses = new HashMap<>();
        HashMap<String, Integer> boneIndices = new HashMap<>();

        materialParser.read(node, materials ->
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            String sourceId = "#" + source;
            DrawingAttributeBuffer buffer = new TexturedDrawingAttributeBuffer(
                gl.texture(storage.open(materials.textureFile(material))),
                gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX").fromRightToLeftHanded())
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL").fromRightToLeftHanded()));
            
            var mesh = RawMesh.of(buffer, intBuffers.map("INDEX_VERTEX"));
            var ex = meshes.get(sourceId);
            if(ex != null) mesh = RawMesh.many(ex, mesh);
            
            meshes.put(sourceId, mesh);
        }));

        skinParser.read(node, (skinId, sourceId, names, invBind, matrix, weights, joints, weightPerIndex) ->
        {
	        new RangeFlow(names.length()).read(i -> boneIndices.put(names.at(i), i));
	        invBindPoses.put("#" + skinId, invBind);
        	
            var rawMesh = meshes.get(sourceId);
            if(rawMesh != null) rawMesh.accept((mesh, indices) ->
            {
                Mapping<Buffer<Float>, Buffer<Float>> mapBuffer = buffer ->
                    new MapBuffer<>(new RangeBuffer(indices.length() * weightPerIndex), i ->
                    {
                        int pos = i / weightPerIndex;
                        int add = i % weightPerIndex;
                        int index = indices.at(pos);
                        try
                        {
                            return buffer.at(index * weightPerIndex + add);
                        }
                        catch(Throwable th)
                        {
                            System.out.println("error");
                            return 0f;
                        }
                    });
                Buffer<Float> ordered_joints = mapBuffer.map(new MapBuffer<>(joints, j -> (float)j));
                Buffer<Float> ordered_weights = mapBuffer.map(weights);

                var meshId = "#" + skinId;
                var skinnedMesh = RawMesh.of(mesh
	                .attribute(weightPerIndex, ordered_joints)
	                .attribute(weightPerIndex, ordered_weights), indices);
                var ex = skinnedMeshes.get(meshId);
                if(ex != null) skinnedMesh = RawMesh.many(ex, skinnedMesh);
                
                skinnedMeshes.put(meshId, skinnedMesh);
            });
        });
        
        ArrayList<Drawing> simpleDrawings = new ArrayList<Drawing>();
        ArrayList<Drawing> skinnedDrawings = new ArrayList<Drawing>();
        HashMap<Drawing, String> objectAnimKeys = new HashMap<>();
        
        class SceneReader implements NodeReader
        {
			@Override
			public void read(ColladaNode child) {
				child.children("instance_geometry",
					geom -> geom.attribute("url",
						url ->
						{
							meshes.get(url).accept((mesh, indices) ->
							{
								var drawing = mesh.drawing();
								simpleDrawings.add(drawing);
								child.attribute("name", name -> objectAnimKeys.put(drawing, name));
							});
						}));
				child.children("instance_controller",
					geom -> geom.attribute("url",
						url ->
						{
							skinnedMeshes.get(url).accept((mesh, indices) -> skinnedDrawings.add(mesh.drawing()));
						}));
				child.children("node", this);
			}
        }
        
        node.children("COLLADA",
    		root -> root.children("library_visual_scenes",
    		scenes -> scenes.children("visual_scene",
			scene -> scene.children("node", new SceneReader()))));
        
        return (skinShader, staticShader) ->
        {	
            int MAX_MATRICES = 100;

            var skinDrawing = new CompositeDrawing(Flow.iterable(skinnedDrawings));
            
            var jointTransformsUniform = skinShader.uniforms().uniformMatrixArray("jointTransforms", MAX_MATRICES);
            var staticTransformUniform = staticShader.uniforms().uniformMatrix("transform");

            return (skinAnimation, objectsAnimation, shaderInitializer) ->
            {
                Matrix4f[] bones = new Matrix4f[MAX_MATRICES];
                for(int i = 0; i < MAX_MATRICES; i++) bones[i] = Matrix4f.identity;

                Skeleton invBind = invBindPoses.entrySet().iterator().next().getValue();

                Flow.iterable(boneIndices.entrySet()).read(bone ->
                {
                    String key = bone.getKey();
                    int index = bone.getValue();
                    Matrix4f matrix = skinAnimation.transform(key).mul(invBind.transform(key));
                    bones[index] = matrix;
                });

                var shadedSkinDrawing = skinShader.play(() ->
                {
                	shaderInitializer.draw();
                    jointTransformsUniform.load(new ArrayBuffer<>(bones));
                    skinDrawing.draw();
                });
                
                var shadedObjectsDrawing = staticShader.play(() ->
                {
                	shaderInitializer.draw();
                	for(Drawing drawing : simpleDrawings)
                	{
                		String animKey = objectAnimKeys.get(drawing);
                		Matrix4f mat = animKey == null ? Matrix4f.identity : objectsAnimation.transform(animKey);
                		staticTransformUniform.load(mat);
                		drawing.draw();
                	}
                });
                
                return new CompositeDrawing(shadedSkinDrawing, shadedObjectsDrawing);
            };
        };
    }
}