package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.function.Condition;
import nine.geometry.Animation;
import nine.geometry.AnimationSource;
import nine.geometry.Animator;
import nine.math.Matrix4f;

public class ColladaBoneNodeReader implements NodeReader
{
    AnimationSource parent;
    ColladaBoneReader reader;
    Animator animator;
    NodeReader controllerReader;
    Condition<String> boneType;

    public ColladaBoneNodeReader(
    		Condition<String> boneType,
    		AnimationSource parent,
    		Animator animator,
    		ColladaBoneReader boneReader,
    		NodeReader controllerReader)
    {
        this.parent = parent;
        this.reader = boneReader;
        this.animator = animator;
        this.controllerReader = controllerReader;
        this.boneType = boneType;
    }

    @Override
    public void read(ColladaNode child)
    {
        child.attribute("id", id ->
        child.attribute("name", name ->
        {
            child.attribute("type", type ->
            child.children("matrix", matrix ->
            matrix.content(content ->
            {
                Animation animation = animator.animation(id, name);
                Animation local;
                
                if(animation != null)
                {
                    local = animation;
                }
                else
                {
                    Matrix4f contentMatrix = Matrix4f.from_COLLADA_Buffer(new TextValueBuffer<>(content, Float::parseFloat), 0);
                    local = time -> contentMatrix;
                }
                
                AnimationSource transform = refresh ->
                {
                	var p = parent.instance(refresh);
                	Animation anim = time -> p.animate(time).mul(local.animate(time));
                	return anim.refreshable(refresh);
                };
                if(boneType.match(type))
                {
                    reader.read(name, transform);
                }
                
                child.children("node", new ColladaBoneNodeReader(boneType, transform, animator, reader, controllerReader));
            })));
            child.children("instance_controller", controllerReader);
        }));
    }
}