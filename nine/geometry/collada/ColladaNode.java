package nine.geometry.collada;

public interface ColladaNode
{
    void attribute(String name, StringReader reader);
    void content(StringReader reader);
    void children(String tag, NodeReader reader);

    public interface ChildrenSelector
    {
        void select(StringReader reader);
    }

    default void manyChildren(ChildrenSelector selector, NodeReader reader)
    {
        selector.select(tag -> children(tag, reader));
    }
}