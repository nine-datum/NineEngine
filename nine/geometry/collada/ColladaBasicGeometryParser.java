package nine.geometry.collada;

import java.util.HashMap;

import nine.buffer.Buffer;
import nine.buffer.FlowToBuffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.buffer.TextValueBuffer;
import nine.collection.FlatmapFlow;
import nine.collection.Flow;
import nine.collection.RangeFlow;
import nine.function.IntegerMapping;

public class ColladaBasicGeometryParser implements ColladaGeometryParser
{
    @Override
    public void read(ColladaNode node, BuffersReader reader)
    {
        node.children("COLLADA", root ->
            root.children("library_geometries", lib ->
            lib.children("geometry", geom ->
            geom.attribute("id", geomId ->
            geom.children("mesh", mesh ->
        {
            HashMap<String, IntegerMapping<Flow<Float>>> sources = new HashMap<String, IntegerMapping<Flow<Float>>>();

            mesh.children("source", source ->
                source.attribute("id", id ->
                source.children("float_array", array ->
                source.children("technique_common", tech ->
                tech.children("accessor", accessor ->
                accessor.attribute("stride", stride ->
                array.content(content ->
            {
                Buffer<Float> buffer = new TextValueBuffer<Float>(content, Float::parseFloat);
                int step = Integer.parseInt(stride);
                sources.put(id, i -> a ->
                {
                    int m = i * step;
                    for(int c = 0; c < step; c++) a.call(buffer.at(m + c));
                });
            })))))));

            mesh.children("vertices", vertices ->
                vertices.attribute("id", id ->
                vertices.children("input", input ->
                input.attribute("source", source ->
            {
                String s = source.replaceFirst("#", "");
                sources.put(id, sources.get(s));
                sources.remove(s);
            }))));

            int elementsCount = sources.size();

            NodeReader trianglesReader = triangles -> triangles.children("p", p -> p.content(pContent ->
            triangles.attribute("material", material ->
            {
                MutableBufferMapping<Integer> intBuffers = new MutableBufferMapping<Integer>();
                MutableBufferMapping<Float> floatBuffers = new MutableBufferMapping<Float>();
                Buffer<Integer> rawIndices = new TextValueBuffer<Integer>(pContent, Integer::parseInt);
                int indicesCount = rawIndices.length();
                intBuffers.write("INDEX", new RangeBuffer(indicesCount));
                intBuffers.write("INDEX_RAW", rawIndices);
                intBuffers.write("INDEX_VERTEX", new MapBuffer<>(
                    new RangeBuffer(indicesCount / elementsCount),
                    i -> rawIndices.at(i * elementsCount)));

                triangles.children("input", input ->
                {
                    input.attribute("semantic", semantic ->
                    input.attribute("source", source ->
                    input.attribute("offset", offset ->
                    {
                        int step = Integer.parseInt(offset);
                        IntegerMapping<Flow<Float>> sourceBuffer = sources.get(source.replaceFirst("#", ""));
                        floatBuffers.write(semantic,
                            new FlowToBuffer<>(
                                new FlatmapFlow<>(
                                    new RangeFlow(indicesCount / elementsCount),
                                    i -> sourceBuffer.at(rawIndices.at(i * elementsCount + step)))));
                    })));
                });

                reader.read(geomId, material, floatBuffers, intBuffers);
            })));
            mesh.children("triangles", trianglesReader);
            mesh.children("polylist", trianglesReader);
        })))));
    }
}