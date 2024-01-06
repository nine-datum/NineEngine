package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nine.buffer.Buffer;
import nine.buffer.CachedBuffer;
import nine.buffer.EmptyBuffer;
import nine.buffer.FloatArrayBuffer;
import nine.buffer.IntegerArrayBuffer;
import nine.buffer.MapBuffer;
import nine.buffer.MatrixBuffer;
import nine.buffer.TextElementsBuffer;
import nine.buffer.TextValueBuffer;
import nine.collection.RangeFlow;
import nine.math.Matrix4f;

public class ColladaBasicSkinParser implements ColladaSkinParser
{
    public void read(ColladaNode node, SkinReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_controllers", lib ->
        lib.children("controller", controller ->
        controller.attribute("id", skinId ->
        controller.children("skin", skin ->
        skin.attribute("source", skinSource ->
        {
            List<Buffer<String>> names = new ArrayList<>(List.of(new EmptyBuffer<String>()));
            MutableBufferMapping<String> buffers = new MutableBufferMapping<String>();

            skin.children("bind_shape_matrix", matrix ->
            matrix.content(matrixContent ->
            {
                buffers.write("BIND_MATRIX", new TextElementsBuffer(matrixContent));
            }));

            skin.children("source", source ->
            source.attribute("id", id ->
            source.children("technique_common", teq ->
            teq.children("accessor", accessor ->
            {
                source.children("Name_array", namesSource ->
                namesSource.content(namesContent ->
                {
                    names.set(0, new TextValueBuffer<String>(namesContent, s -> s));
                }));

                source.children("float_array", array ->
                array.content(content ->
                {
                    buffers.write("#" + id, new TextElementsBuffer(content));
                }));
            }))));

            skin.children("vertex_weights", weights ->
            skin.children("joints", joints ->
            {
                NodeReader inputReader = input ->
                input.attribute("source", source ->
                input.attribute("semantic", semantic ->
                {
                    buffers.write(semantic, buffers.map(source));
                }));
                weights.children("input", inputReader);
                joints.children("input", inputReader);
                
                weights.children("vcount", vcount ->
                weights.children("v", v ->
                vcount.content(vcountContent ->
                v.content(vContent ->
                {
                    Buffer<Integer> counts = new TextValueBuffer<Integer>(vcountContent, Integer::parseInt);
                    Buffer<Integer> indices = new TextValueBuffer<Integer>(vContent, Integer::parseInt);
                    Buffer<Float> rawWeights = new CachedBuffer<>(new MapBuffer<>(buffers.map("WEIGHT"), Float::parseFloat));
                    int verticesCount = counts.length();

                    int weightsPerVertex = 4;

                    float[] weightArray = new float[verticesCount * weightsPerVertex];
                    int[] jointArray = new int[verticesCount * weightsPerVertex];

                    int index = 0;
                    int rawIndex = 0;
                    int countIndex = 0;
                    while(countIndex < verticesCount)
                    {
                        int count = counts.at(countIndex++);

                        for(int i = 0; i < weightsPerVertex; i++)
                        {
                            if(i < count)
                            {
                                int ri = (rawIndex + i) * 2;
                                jointArray[index + i] = indices.at(ri);
                                weightArray[index + i] = rawWeights.at(indices.at(ri + 1));
                            }
                            else
                            {
                                jointArray[index + i] = -1;
                            }
                        }

                        index += weightsPerVertex;
                        rawIndex += count;
                    }

                    Buffer<Float> matrixBuffer = new MapBuffer<>(buffers.map("BIND_MATRIX"), Float::parseFloat);
                    Buffer<Matrix4f> invPoses = new MatrixBuffer(
                        new MapBuffer<>(
                            buffers.map("INV_BIND_MATRIX"),
                            Float::parseFloat));
                    
                    HashMap<String, Matrix4f> inv_bind_poses = new HashMap<>();
                    new RangeFlow(names.get(0).length()).read(i ->
                    {
                        inv_bind_poses.put(names.get(0).at(i), invPoses.at(i));
                    });

                    reader.read(
                        skinId,
                        skinSource,
                        names.get(0),
                        key -> inv_bind_poses.get(key),
                        Matrix4f.from_COLLADA_Buffer(matrixBuffer, 0),
                        new FloatArrayBuffer(weightArray),
                        new IntegerArrayBuffer(jointArray),
                        weightsPerVertex);
                }))));
            }));
        }))))));
    }
}