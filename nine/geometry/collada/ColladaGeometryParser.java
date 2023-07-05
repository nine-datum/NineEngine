package nine.geometry.collada;

public interface ColladaGeometryParser
{
    void read(ColladaNode node, BuffersReader reader);
}