package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nine.buffer.Buffer;
import nine.buffer.EmptyBuffer;
import nine.buffer.FlowToBuffer;
import nine.buffer.MapBuffer;
import nine.collection.BufferToFlow;
import nine.collection.CachedFlow;
import nine.collection.FlatmapFlow;
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

    public ColladaSkinnedModel(ColladaNode node, ColladaGeometryParser geometryParser, ColladaSkinParser skinParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
    }

    @Override
    public Drawing load(OpenGL gl)
    {
        HashMap<String, DrawingAttributeBuffer> map = new HashMap<>();
        List<Buffer<Integer>> raw_indices = new ArrayList<>(List.of(new EmptyBuffer<>()));

        geometryParser.read(node, (source, floatBuffers, intBuffers) ->
        {
            raw_indices.set(0, intBuffers.map("INDEX_VERTEX"));
            map.put("#" + source, gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL")));
        });

        skinParser.read(node, (source, names, matrix, weights, joints, weightPerIndex) ->
        {
            Buffer<Integer> indices = raw_indices.get(0);
            Mapping<Buffer<Float>, Buffer<Float>> mapBuffer = buffer ->
                new FlowToBuffer<>(new FlatmapFlow<>(new BufferToFlow<>(indices), i -> action ->
                {
                    int w = i * weightPerIndex;
                    for(int c = 0; c < weightPerIndex; c++)
                    {
                        action.call(buffer.at(w + c));
                    }
                }));
            Buffer<Float> ordered_joints = mapBuffer.map(new MapBuffer<>(joints, j -> (float)j));
            Buffer<Float> ordered_weights = mapBuffer.map(weights);

            map.put(source, map.get(source)
                .attribute(weightPerIndex, ordered_joints)
                .attribute(weightPerIndex, ordered_weights));
        });

        return new CompositeDrawing(
            new CachedFlow<>(
                new MapFlow<>(
                    new IterableFlow<>(map.values()),
                    m -> m.drawing())));
    }
}