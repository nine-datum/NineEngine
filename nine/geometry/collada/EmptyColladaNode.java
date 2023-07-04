package nine.geometry.collada;

public class EmptyColladaNode implements ColladaNode
{
    @Override
    public void attribute(String name, StringReader reader){}
    @Override
    public void content(StringReader reader){}
    @Override
    public void children(String tag, NodeReader reader){}
}