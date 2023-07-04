package nine.geometry.collada;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import nine.buffer.Buffer;
import nine.buffer.EmptyBuffer;
import nine.buffer.FlowToBuffer;
import nine.buffer.RangeBuffer;
import nine.buffer.TextValueBuffer;
import nine.collection.FlatmapFlow;
import nine.collection.Flow;
import nine.collection.IterableFlow;
import nine.collection.Mapping;
import nine.collection.RangeFlow;
import nine.function.ErrorHandler;
import nine.function.IntegerMapping;
import nine.geometry.Model;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;

public class ColladaModel implements Model
{
    ColladaNode node;

    public ColladaModel(File source, ErrorHandler errorHandler)
    {
        try
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            node = new XML_ColladaNode(document);
        }
        catch(Throwable error)
        {
            errorHandler.call(error);
            node = new EmptyColladaNode();
        }
    }

    @Override
    public Drawing load(OpenGL gl)
    {
        List<Drawing> drawings = new ArrayList<Drawing>();

        node.children("COLLADA", root ->
            root.children("library_geometries", geom ->
                geom.children("mesh", mesh ->
        {
            List<Buffer<Float>> buffers = List.of(
                new EmptyBuffer<Float>(), // positions
                new EmptyBuffer<Float>(), // normals
                new EmptyBuffer<Float>(), // uvs
                new EmptyBuffer<Float>() // not used
            );
            Buffer<Integer> indices = new EmptyBuffer<Integer>();

            Mapping<String, Integer> semanticIndex = s ->
            {
                switch(s)
                {
                    case "VERTEX": return 0;
                    case "NORMAL": return 1;
                    case "TEXCOORD": return 2;
                }
                return 3;
            };

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
                    a.call(buffer.at(m));
                    a.call(buffer.at(m + 1));
                    a.call(buffer.at(m + 2));
                });
            })))))));

            mesh.children("vertices", vertices ->
                vertices.attribute("id", id ->
                vertices.children("input", input ->
                input.attribute("source", source ->
            {
                sources.put(id, sources.get(source));
            }))));

            int elementsCount = sources.size();

            mesh.children("triangles", triangles -> triangles.children("p", p -> p.content(pContent ->
            {
                Buffer<Integer> rawIndices = new TextValueBuffer<Integer>(pContent, Integer::parseInt);
                int indicesCount = rawIndices.length();

                triangles.children("input", input ->
                {
                    input.attribute("semantic", semantic ->
                    input.attribute("source", source ->
                    input.attribute("offset", offset ->
                    {
                        int step = Integer.parseInt(offset);
                        IntegerMapping<Flow<Float>> sourceBuffer = sources.get(source.replaceFirst("#", ""));
                        buffers.set(semanticIndex.map(semantic),
                            new FlowToBuffer<>(
                                new FlatmapFlow<>(
                                    new RangeFlow(indicesCount / elementsCount),
                                    i -> sourceBuffer.at(indices.at(i * elementsCount + step)))));
                    })));
                });

                drawings.add(gl.vao(new RangeBuffer(indicesCount))
                    .attribute(3, buffers.get(semanticIndex.map("VERTEX")))
                    .attribute(2, buffers.get(semanticIndex.map("TEXCOORD")))
                    .attribute(3, buffers.get(semanticIndex.map("NORMAL")))
                    .drawing());
            })));
        })));

        return new CompositeDrawing(new IterableFlow<Drawing>(drawings));
    }
}
