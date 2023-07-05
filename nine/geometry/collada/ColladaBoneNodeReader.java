package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.math.Matrix4f;
import nine.math.Matrix4fDelayed;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fRowBuffer;
import nine.math.Time;

public class ColladaBoneNodeReader implements NodeReader
{
    Matrix4f parent;
    ColladaBoneReader reader;
    Animator animator;
    NodeReader controllerReader;

    public ColladaBoneNodeReader(Matrix4f parent, Animator animator, ColladaBoneReader boneReader, NodeReader controllerReader)
    {
        this.parent = parent;
        this.reader = boneReader;
        this.animator = animator;
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
                Matrix4f local;

                Animation animation = animator.animation(name);
                if(animation != null)
                {
                    local = animation.animate(new Time());
                }
                else
                {
                    local =
                        new Matrix4fRowBuffer(
                            new TextValueBuffer<>(content, Float::parseFloat));
                }

                Matrix4f transform = new Matrix4fDelayed(new Matrix4fMul(parent, local), 0.033f);

                reader.read(name, transform);
                child.children("node", new ColladaBoneNodeReader(transform, animator, reader, controllerReader));
            }));
            child.children("instance_controller", controllerReader);
        });
    }
}