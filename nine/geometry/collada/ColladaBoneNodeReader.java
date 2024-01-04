package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.function.RefreshStatus;
import nine.math.Matrix4f;

public class ColladaBoneNodeReader implements NodeReader
{
    Animation parent;
    ColladaBoneReader reader;
    Animator animator;
    NodeReader controllerReader;
    RefreshStatus refresh;

    public ColladaBoneNodeReader(Animation parent, Animator animator, RefreshStatus refresh, ColladaBoneReader boneReader, NodeReader controllerReader)
    {
        this.parent = parent;
        this.reader = boneReader;
        this.animator = animator;
        this.controllerReader = controllerReader;
        this.refresh = refresh;
    }

    @Override
    public void read(ColladaNode child)
    {
        child.attribute("type", type ->
        child.attribute("id", id ->
        child.attribute("name", name ->
        {
            child.children("matrix", matrix ->
            matrix.content(content ->
            {
                Animation animation = animator.animation(id);
                Animation local;
                if(animation != null)
                {
                    local = animation;
                }
                else
                {
                    local = time -> Matrix4f.fromBuffer(new TextValueBuffer<>(content, Float::parseFloat), 0);
                }
                Animation transform = time -> parent.animate(time).mul(local.animate(time));

                if(type.equals("JOINT"))
                {
                    reader.read(name, transform);
                }
                
                child.children("node", new ColladaBoneNodeReader(transform, animator, refresh, reader, controllerReader));
            }));
            child.children("instance_controller", controllerReader);
        })));
    }
}