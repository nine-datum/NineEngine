package nine.geometry.collada;

import java.util.HashMap;

import nine.buffer.Buffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.CachedFlow;
import nine.collection.IterableFlow;
import nine.collection.MapFlow;
import nine.collection.Mapping;
import nine.geometry.Model;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;

public class ColladaSkinnedModel implements Model
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaSkeletonParser skeletonParser;

    public ColladaSkinnedModel(ColladaNode node, ColladaGeometryParser geometryParser, ColladaSkinParser skinParser, ColladaSkeletonParser skeletonParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.skeletonParser = skeletonParser;
    }

    @Override
    public Drawing load(OpenGL gl)
    {
        HashMap<String, DrawingAttributeBuffer> map = new HashMap<>();
        MutableBufferMapping<Integer> raw_indices = new MutableBufferMapping<>();

        geometryParser.read(node, (source, floatBuffers, intBuffers) ->
        {
            String sourceId = "#" + source;
            raw_indices.write(sourceId, intBuffers.map("INDEX_VERTEX"));
            map.put(sourceId, gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL")));
        });

        skinParser.read(node, (skinId, sourceId, names, matrix, weights, joints, weightPerIndex) ->
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

            map.put(skinId, map.get(sourceId)
                .attribute(weightPerIndex, ordered_joints)
                .attribute(weightPerIndex, ordered_weights));
        });

        skeletonParser.read(node, (skinId, skeleton) ->
        {
        });

        return new CompositeDrawing(
            new CachedFlow<>(
                new MapFlow<>(
                    new IterableFlow<>(map.values()),
                    m -> m.drawing())));
    }
}