package nine.geometry.collada;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XML_ColladaNode implements ColladaNode
{
    Node node;

    public XML_ColladaNode(Node node)
    {
        this.node = node;
    }

    @Override
    public void attribute(String name, StringReader reader)
    {
        reader.read(node.getAttributes().getNamedItem(name).getNodeValue());
    }

    @Override
    public void children(String tag, NodeReader reader)
    {
        NodeList children = node.getChildNodes();
        int length = children.getLength();
        for(int i = 0; i < length; i++)
        {
            Node child = children.item(i);
            if(child.getNodeName().equals(tag))
            {
                reader.read(new XML_ColladaNode(child));
            }
        }
    }

    @Override
    public void content(StringReader reader)
    {
        reader.read(node.getTextContent());
    }
}