package nine.geometry.collada;

import nine.collection.ArrayFlow;
import nine.collection.Flow;

public class CompositeColladaNode implements ColladaNode
{
    Flow<ColladaNode> nodes;

    public CompositeColladaNode(ColladaNode... nodes)
    {
        this.nodes = new ArrayFlow<ColladaNode>(nodes);
    }
    public CompositeColladaNode(Flow<ColladaNode> nodes)
    {
        this.nodes = nodes;
    }

    @Override
    public void attribute(String name, StringReader reader)
    {
        nodes.read(node -> node.attribute(name, reader));
    }

    @Override
    public void content(StringReader reader)
    {
        nodes.read(node -> node.content(reader));
    }

    @Override
    public void children(String tag, NodeReader reader)
    {
        nodes.read(node -> node.children(tag, reader));
    }
}