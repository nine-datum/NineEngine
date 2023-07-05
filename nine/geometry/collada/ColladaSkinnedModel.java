package nine.geometry.collada;

import java.util.HashMap;
import java.util.Map;

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
import nine.geometry.Model;
import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fMulChain;
import nine.math.Time;
import nine.opengl.CompositeDrawing;
import nine.opengl.CompositeUniform;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;
import nine.opengl.ShaderPlayer;
import nine.opengl.Uniform;

public class ColladaSkinnedModel implements Model
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaSkeletonParser skeletonParser;
    ColladaAnimationParser animationParser;

    public ColladaSkinnedModel(
        ColladaNode node,
        ColladaGeometryParser geometryParser,
        ColladaSkinParser skinParser,
        ColladaSkeletonParser skeletonParser,
        ColladaAnimationParser animationParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.skeletonParser = skeletonParser;
        this.animationParser = animationParser;
    }
    public ColladaSkinnedModel(ColladaNode node)
    {
        this(node,
            new ColladaBasicGeometryParser(),
            new ColladaBasicSkinParser(),
            new ColladaBasicSkeletonParser(),
            new ColladaBasicAnimationParser());
    }

    @Override
    public Drawing load(OpenGL gl, ShaderPlayer shader)
    {
        HashMap<String, DrawingAttributeBuffer> map = new HashMap<>();
        MutableBufferMapping<Integer> raw_indices = new MutableBufferMapping<>();
        HashMap<String, Animation> animations = new HashMap<>();
        HashMap<String, Skeleton> invBindPoses = new HashMap<>();
        HashMap<String, Integer> boneIndices = new HashMap<>();

        geometryParser.read(node, (source, floatBuffers, intBuffers) ->
        {
            String sourceId = "#" + source;
            raw_indices.write(sourceId, intBuffers.map("INDEX_VERTEX"));
            map.put(sourceId, gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL")));
        });

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

            map.put("#" + skinId, map.get(sourceId)
                .attribute(weightPerIndex, ordered_joints)
                .attribute(weightPerIndex, ordered_weights));

            new RangeFlow(names.length()).read(i -> boneIndices.put(names.at(i), i));
            invBindPoses.put("#" + skinId, invBind);
        });

        animationParser.read(node, animations::put);

        skeletonParser.read(node, (skinId, skeleton) ->
        {
            DrawingAttributeBuffer skin = map.get(skinId);
            Matrix4f[] bones = new Collector<>(Matrix4f[]::new)
                .collect(new MapBuffer<>(new RangeBuffer(100), i -> Matrix4fIdentity.identity));

            Skeleton invBind = invBindPoses.get(skinId);
            
            Flow<Uniform> uniforms = new CachedFlow<Uniform>(new MapFlow<>(
                new IterableFlow<Map.Entry<String, Integer>>(boneIndices.entrySet()),
                bone ->
                {
                    String key = bone.getKey();
                    Animation animation = animations.get(key);
                    Matrix4f matrix;
                    if(animation == null)
                    {
                        matrix = new Matrix4fMul(skeleton.transform(key), invBind.transform(key));
                    }
                    else
                    {
                         matrix = new Matrix4fMulChain(
                            animation.animate(new Time()),
                            skeleton.transform(key),
                            invBind.transform(key));
                    }
                    int index = bone.getValue();
                    return () ->
                    {
                        bones[index] = matrix;
                    };
                }
            ));

            map.put(skinId, new ShadedDrawingAttributeBuffer(skin, shader.uniforms(u ->
            {
                return new CompositeUniform(
                    new CompositeUniform(uniforms),
                    u.uniformMatrixArray("jointTransforms", new ArrayBuffer<>(bones)));
            })));
        });

        return shader.play(new CompositeDrawing(
            new CachedFlow<>(
                new MapFlow<>(
                    new IterableFlow<>(map.values()),
                    m -> m.drawing()))));
    }
}