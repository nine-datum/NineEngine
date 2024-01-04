package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nine.buffer.ArrayBuffer;
import nine.buffer.Buffer;
import nine.buffer.Collector;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.CachedFlow;
import nine.collection.Flow;
import nine.collection.IterableFlow;
import nine.collection.MapFlow;
import nine.collection.Mapping;
import nine.collection.RangeFlow;
import nine.function.ActionSingle;
import nine.function.ActionTrio;
import nine.geometry.SkinnedModel;
import nine.geometry.SkinnedModelAsset;
import nine.io.Storage;
import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;
import nine.opengl.Texture;

public class ColladaSkinnedModel implements SkinnedModelAsset
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaSkeletonParser skeletonParser;
    ColladaAnimationParser animationParser;
    ColladaMaterialParser materialParser;

    public ColladaSkinnedModel(
        ColladaNode node,
        ColladaGeometryParser geometryParser,
        ColladaSkinParser skinParser,
        ColladaSkeletonParser skeletonParser,
        ColladaAnimationParser animationParser,
        ColladaMaterialParser materialParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.skeletonParser = skeletonParser;
        this.animationParser = animationParser;
        this.materialParser = materialParser;
    }
    public ColladaSkinnedModel(ColladaNode node)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            new ColladaBasicSkeletonParser(),
            new ColladaBasicAnimationParser(),
            new ColladaBasicMaterialParser());
    }
    public ColladaSkinnedModel(ColladaNode node, ColladaAnimationParser animationParser)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            new ColladaBasicSkeletonParser(),
            animationParser,
            new ColladaBasicMaterialParser());
    }

    interface RawMeshAction
    {
        void call(String id, DrawingAttributeBuffer buffer, Buffer<Integer> rawIndices);
    }
    interface RawMesh
    {
        void accept(RawMeshAction action);
    }

    @Override
    public SkinnedModel load(OpenGL gl, Storage storage)
    {
        class TexturedDrawingAttributeBuffer implements DrawingAttributeBuffer
        {
            Texture texture;
            DrawingAttributeBuffer source;

            public TexturedDrawingAttributeBuffer(Texture texture, DrawingAttributeBuffer source) {
                this.texture = texture;
                this.source = source;
            }

            @Override
            public DrawingAttributeBuffer attribute(int stride, Buffer<Float> data)
            {
                return new TexturedDrawingAttributeBuffer(texture, source.attribute(stride, data));
            }

            @Override
            public Drawing drawing()
            {
                return texture.apply(source.drawing());
            }
        }

        ArrayList<RawMesh> meshes = new ArrayList<RawMesh>();

        class AddRawMeshAction
        {
            List<RawMesh> meshes;

            public AddRawMeshAction(List<RawMesh> meshes)
            {
                this.meshes = meshes;
            }

            void call(String id, ActionTrio<DrawingAttributeBuffer, Buffer<Integer>, ActionSingle<RawMesh>> action)
            {
                ArrayList<RawMesh> append = new ArrayList<>();
                ArrayList<RawMesh> remove = new ArrayList<>();
                meshes.forEach(m -> m.accept((meshId, mesh, rawIndices) ->
                {
                    if(meshId.equals(id))
                    {
                        action.call(mesh, rawIndices, append::add);
                        remove.add(m);
                    }
                }));
                meshes.removeAll(remove);
                meshes.addAll(append);
            }
        }
        AddRawMeshAction addRawMeshAction = new AddRawMeshAction(meshes);

        HashMap<String, Skeleton> invBindPoses = new HashMap<>();
        HashMap<String, Integer> boneIndices = new HashMap<>();

        materialParser.read(node, materials ->
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            String sourceId = "#" + source;
            DrawingAttributeBuffer buffer = new TexturedDrawingAttributeBuffer(
                gl.texture(storage.open(materials.textureFile(material))),
                gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL")));
            meshes.add(action -> action.call(sourceId, buffer, intBuffers.map("INDEX_VERTEX")));
        }));

        skinParser.read(node, (skinId, sourceId, names, invBind, matrix, weights, joints, weightPerIndex) ->
        {
            addRawMeshAction.call(sourceId, (mesh, indices, meshAction) ->
            {
                Mapping<Buffer<Float>, Buffer<Float>> mapBuffer = buffer ->
                    new MapBuffer<>(new RangeBuffer(indices.length() * weightPerIndex), i ->
                    {
                        int pos = i / weightPerIndex;
                        int add = i % weightPerIndex;
                        int index = indices.at(pos);
                        return buffer.at(index * weightPerIndex + add);
                    });
                Buffer<Float> ordered_joints = mapBuffer.map(new MapBuffer<>(joints, j -> (float)j));
                Buffer<Float> ordered_weights = mapBuffer.map(weights);

                meshAction.call(action -> action.call("#" + skinId, mesh
                    .attribute(weightPerIndex, ordered_joints)
                    .attribute(weightPerIndex, ordered_weights), indices));

                new RangeFlow(names.length()).read(i -> boneIndices.put(names.at(i), i));
                invBindPoses.put("#" + skinId, invBind);
            });
        });
        
        return animation -> shader ->
        {
            Matrix4f[] bones = new Collector<>(Matrix4f[]::new)
                .collect(new MapBuffer<>(new RangeBuffer(100), i -> Matrix4fIdentity.identity));

            Skeleton invBind = invBindPoses.entrySet().iterator().next().getValue();

            Flow.iterable(boneIndices.entrySet()).read(bone ->
            {
                String key = bone.getKey();
                int index = bone.getValue();
                Matrix4f matrix = animation.transform(key).mul(invBind.transform(key));
                bones[index] = matrix;
            });

            ArrayList<DrawingAttributeBuffer> drawings = new ArrayList<>();
            meshes.forEach(a -> a.accept((id, mesh, indices) -> drawings.add(mesh)));

            var shaderPlayer = shader.uniforms(u -> u.uniformMatrixArray("jointTransforms", new ArrayBuffer<>(bones)));

            return shaderPlayer.play(new CompositeDrawing(
                new CachedFlow<>(
                    new MapFlow<>(
                        new IterableFlow<>(drawings),
                        m -> m.drawing()))));
        };
    }
}