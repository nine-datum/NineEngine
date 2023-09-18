package nine.geometry.collada;

import java.util.HashMap;
import java.util.Map;

import nine.buffer.ArrayBuffer;
import nine.buffer.Buffer;
import nine.buffer.Collector;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.CachedFlow;
import nine.collection.ConcatFlow;
import nine.collection.Flow;
import nine.collection.IterableFlow;
import nine.collection.MapFlow;
import nine.collection.Mapping;
import nine.collection.RangeFlow;
import nine.collection.SingleItemFlow;
import nine.collection.UnrollFlow;
import nine.geometry.SkinnedModel;
import nine.geometry.SkinnedModelAsset;
import nine.io.Storage;
import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fRefreshable;
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

        HashMap<String, Flow<DrawingAttributeBuffer>> map = new HashMap<>();
        MutableBufferMapping<Integer> raw_indices = new MutableBufferMapping<>();
        HashMap<String, Animation> animations = new HashMap<>();
        HashMap<String, Skeleton> invBindPoses = new HashMap<>();
        HashMap<String, Integer> boneIndices = new HashMap<>();

        materialParser.read(node, materials ->
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            String sourceId = "#" + source;
            raw_indices.write(sourceId, intBuffers.map("INDEX_VERTEX"));
            Flow<DrawingAttributeBuffer> flow = new SingleItemFlow<>(new TexturedDrawingAttributeBuffer(
                gl.texture(storage.open("models/textures/" + materials.textureFile(material))),
                gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL"))));
            if(map.containsKey(sourceId))
            {
                flow = new ConcatFlow<>(map.get(sourceId), flow);
            }
            map.put(sourceId, flow);
        }));

        skinParser.read(node, (skinId, sourceId, names, invBind, matrix, weights, joints, weightPerIndex) ->
        {
            Buffer<Integer> indices = raw_indices.map(sourceId);
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

            map.put("#" + skinId, new MapFlow<>(map.get(sourceId), m -> m
                .attribute(weightPerIndex, ordered_joints)
                .attribute(weightPerIndex, ordered_weights)));
            map.remove(sourceId);

            new RangeFlow(names.length()).read(i -> boneIndices.put(names.at(i), i));
            invBindPoses.put("#" + skinId, invBind);
        });

        animationParser.read(node, animations::put);
        
        return skeletonTransform -> (shader, refreshStatus) ->
        {
            skeletonParser.read(node, animations::get, refreshStatus, (skinId, skeleton) ->
            {
                Flow<DrawingAttributeBuffer> skin = map.get(skinId);
                Skeleton mulSkeleton = key -> new Matrix4fMul(skeletonTransform.transform(key), skeleton.transform(key));

                Matrix4f[] bones = new Collector<>(Matrix4f[]::new)
                    .collect(new MapBuffer<>(new RangeBuffer(100), i -> Matrix4fIdentity.identity));

                Skeleton invBind = invBindPoses.get(skinId);

                new IterableFlow<Map.Entry<String, Integer>>(boneIndices.entrySet()).read(bone ->
                {
                    String key = bone.getKey();
                    int index = bone.getValue();
                    Matrix4f matrix = new Matrix4fRefreshable(
                        new Matrix4fMul(mulSkeleton.transform(key), invBind.transform(key)),
                        refreshStatus);
                    bones[index] = matrix;
                });

                map.put(skinId, new MapFlow<>(skin, s -> new ShadedDrawingAttributeBuffer(s, shader.uniforms(u ->
                    u.uniformMatrixArray("jointTransforms", new ArrayBuffer<>(bones))))));
            });

            return new CompositeDrawing(
                new CachedFlow<>(
                    new MapFlow<>(
                        new UnrollFlow<>(new IterableFlow<>(map.values())),
                        m -> m.drawing())));
        };
    }
}