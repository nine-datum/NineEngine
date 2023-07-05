package nine.geometry.collada;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import nine.collection.IterableFlow;
import nine.function.ErrorHandler;
import nine.geometry.Model;
import nine.io.StorageResource;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;

public class ColladaModel implements Model
{
    ColladaNode node;
    ColladaParser parser;

    public ColladaModel(StorageResource source, ColladaParser parser, ErrorHandler errorHandler)
    {
        this.parser = parser;
        source.read(flow ->
        {
            try
            {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(flow);
                node = new XML_ColladaNode(document);
            }
            catch(Throwable error)
            {
                errorHandler.call(error);
                node = new EmptyColladaNode();
            }
        }, errorHandler);
    }

    @Override
    public Drawing load(OpenGL gl)
    {
        List<Drawing> drawings = new ArrayList<Drawing>();

        parser.read(node, (floatBuffers, intBuffers) ->
        {
            drawings.add(gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL"))
                    .drawing());
        });

        return new CompositeDrawing(new IterableFlow<Drawing>(drawings));
    }
}
