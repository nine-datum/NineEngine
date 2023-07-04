package nine.geometry.collada;

public interface ColladaNode
{
    void attribute(String name, StringReader reader);
    void content(StringReader reader);
    void children(String tag, NodeReader reader);
}