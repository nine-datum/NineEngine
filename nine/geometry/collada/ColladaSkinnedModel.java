package nine.geometry.collada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nine.buffer.ArrayBuffer;
import nine.buffer.Buffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.Flow;
import nine.collection.Mapping;
import nine.collection.RangeFlow;
import nine.drawing.Color;
import nine.drawing.ColorFloatStruct;
import nine.geometry.Material;
import nine.geometry.MaterializedDrawing;
import nine.geometry.Model;
import nine.geometry.ShadedSkinnedModel;
import nine.geometry.Skeleton;
import nine.geometry.SkinnedModelAsset;
import nine.io.Storage;
import nine.math.Matrix4f;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;
import nine.opengl.Profiler;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;

public class ColladaSkinnedModel implements SkinnedModelAsset
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaMaterialParser materialParser;

    public ColladaSkinnedModel(
        ColladaNode node,
        ColladaGeometryParser geometryParser,
        ColladaSkinParser skinParser,
        ColladaMaterialParser materialParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.materialParser = materialParser;
    }
    public ColladaSkinnedModel(ColladaNode node)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            new ColladaBasicMaterialParser());
    }

    interface RawMeshAction
    {
        void call(DrawingAttributeBuffer buffer, Buffer<Integer> rawIndices, String material);
    }
    interface RawMesh
    {
        void accept(RawMeshAction action);

        static RawMesh of(DrawingAttributeBuffer buffer, Buffer<Integer> rawIndices, String material)
        {
            return action -> action.call(buffer, rawIndices, material);
        }
        static RawMesh many(RawMesh...meshes)
        {
        	return a ->
        	{
        		for(RawMesh m : meshes) m.accept(a);
        	};
        }
    }
    interface RawModel
    {
        MaterializedDrawing instance(ShaderPlayer shader);
    }
    
    static RawModel makeModel(String material, Drawing drawing, Profiler profiler)
    {
    	return shader -> materials ->
    	{
    		Material mat = materials.material(material);
    		return mat.apply(shader, drawing);
    	};
    }

    @Override
    public ShadedSkinnedModel load(OpenGL gl)
    {
        HashMap<String, RawMesh> meshes = new HashMap<>();
        HashMap<String, RawMesh> skinnedMeshes = new HashMap<>();

        HashMap<String, Skeleton> invBindPoses = new HashMap<>();
        HashMap<String, Integer> boneIndices = new HashMap<>();
        
        var profiler = gl.profiler();
        
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {	
            String sourceId = "#" + source;
            DrawingAttributeBuffer buffer = gl.vao(intBuffers.map("INDEX"))
                .attribute(3, floatBuffers.map("VERTEX").fromRightToLeftHanded())
                .attribute(2, floatBuffers.map("TEXCOORD"))
                .attribute(3, floatBuffers.map("NORMAL").fromRightToLeftHanded());
            
            var mesh = RawMesh.of(buffer, intBuffers.map("INDEX_VERTEX"), material);
            var ex = meshes.get(sourceId);
            if(ex != null) mesh = RawMesh.many(ex, mesh);
            
            meshes.put(sourceId, mesh);
        });

        skinParser.read(node, (skinId, sourceId, names, invBind, matrix, weights, joints, weightPerIndex) ->
        {
	        new RangeFlow(names.length()).read(i -> boneIndices.put(names.at(i), i));
	        invBindPoses.put("#" + skinId, invBind);
        	
            var rawMesh = meshes.get(sourceId);
            if(rawMesh != null) rawMesh.accept((mesh, indices, material) ->
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
	                .attribute(weightPerIndex, ordered_weights), indices, material);
                var ex = skinnedMeshes.get(meshId);
                if(ex != null) skinnedMesh = RawMesh.many(ex, skinnedMesh);
                
                skinnedMeshes.put(meshId, skinnedMesh);
            });
        });
        
        ArrayList<RawModel> simpleModels = new ArrayList<>();
        ArrayList<RawModel> skinnedModels = new ArrayList<>();
        HashMap<RawModel, String> objectModelAnimKeys = new HashMap<>();
        
        class SceneReader implements NodeReader
        {
			@Override
			public void read(ColladaNode child) {
				child.children("instance_geometry",
					geom -> geom.attribute("url",
					url -> meshes.get(url).accept(
					(mesh, indices, material) ->
					{
						var model = makeModel(material, mesh.drawing(), profiler);
						simpleModels.add(model);
						child.attribute("name", name -> objectModelAnimKeys.put(model, name));
					}
				)));
				child.children("instance_controller",
					geom -> geom.attribute("url",
					url -> skinnedMeshes.get(url).accept(
					(mesh, indices, material) -> skinnedModels.add(
							makeModel(material, mesh.drawing(), profiler)
				))));
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
            
            var jointTransformsUniform = skinShader.uniforms().uniformMatrixArray("jointTransforms", MAX_MATRICES);
            var staticTransformUniform = staticShader.uniforms().uniformMatrix("transform");

            return (skinAnimation, objectsAnimation, root, materials, shaderInitializer) ->
            {
            	var skinDrawing = 
            	new CompositeDrawing(
	        		skinnedModels
	        			.stream()
	        			.map(m -> m.instance(skinShader).materialize(materials))
	        			.toArray(Drawing[]::new));
            
                Matrix4f[] bones = new Matrix4f[MAX_MATRICES];
                for(int i = 0; i < MAX_MATRICES; i++) bones[i] = Matrix4f.identity;

                Skeleton invBind = Skeleton.someOf(invBindPoses.values());

                Flow.iterable(boneIndices.entrySet()).read(bone ->
                {
                    String key = bone.getKey();
                    int index = bone.getValue();
                    var invBindMat = invBind.transform(key);
                    if(invBindMat == null)
                	{
                    	throw new RuntimeException("%s inv bind pose is missing!".formatted(key));
                	}
                    Matrix4f matrix = skinAnimation.transform(key).mul(invBindMat);
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
	                	for(RawModel model : simpleModels)
	                	{
	                		String animKey = objectModelAnimKeys.get(model);
	                		Matrix4f mat = animKey == null ? Matrix4f.identity : objectsAnimation.transform(animKey);
	                		mat = root.mul(mat);
	                		staticTransformUniform.load(mat);
                			var instance = model.instance(staticShader);
                			var matz = instance.materialize(materials);
                			matz.draw();
	                	}
                });

                return new CompositeDrawing(shadedSkinDrawing, shadedObjectsDrawing);
            };
        };
    }
}
