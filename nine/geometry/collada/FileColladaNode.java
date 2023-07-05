package nine.geometry.collada;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import nine.function.ErrorHandler;
import nine.io.StorageResource;

public class FileColladaNode implements ColladaNode
{
    ColladaNode node;

    public FileColladaNode(StorageResource source, ErrorHandler errorHandler)
    {
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
    public void attribute(String name, StringReader reader)
    {
        node.attribute(name, reader);
    }

    @Override
    public void content(StringReader reader)
    {
        node.content(reader);
    }

    @Override
    public void children(String tag, NodeReader reader)
    {
        node.children(tag, reader);
    }
}