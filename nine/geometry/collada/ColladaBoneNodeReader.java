package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fRowBuffer;
import nine.math.Matrix4fStruct;

public class ColladaBoneNodeReader implements NodeReader
{
    Matrix4f parent;
    ColladaBoneReader reader;
    NodeReader controllerReader;

    public ColladaBoneNodeReader(Matrix4f parent, ColladaBoneReader reader, NodeReader controllerReader)
    {
        this.parent = parent;
        this.reader = reader;
        this.controllerReader = controllerReader;
    }

    @Override
    public void read(ColladaNode child)
    {
        child.attribute("name", name ->
        {
            child.children("matrix", matrix ->
            matrix.content(content ->
            {
                Matrix4f local =
                        new Matrix4fRowBuffer(
                            new TextValueBuffer<>(content, Float::parseFloat));
                Matrix4f transform =
                    new Matrix4fStruct(
                                new Matrix4fMul(parent, local));
                reader.read(name, transform);
                child.children("node", new ColladaBoneNodeReader(transform, reader, controllerReader));
            }));
            child.children("instance_controller", controllerReader);
        });
    }
}