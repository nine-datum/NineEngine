package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.function.FunctionDouble;
import nine.function.RefreshStatus;
import nine.geometry.Animation;
import nine.geometry.Animator;
import nine.math.Matrix4f;

public class ColladaBoneNodeReader implements NodeReader
{
    Animation parent;
    ColladaBoneReader reader;
    Animator animator;
    NodeReader controllerReader;
    RefreshStatus refresh;
    String boneType;
    FunctionDouble<String, Animation, Animation> processLocal;

    public ColladaBoneNodeReader(
    		String boneType,
    		Animation parent,
    		Animator animator,
    		RefreshStatus refresh,
    		ColladaBoneReader boneReader,
    		NodeReader controllerReader,
    		FunctionDouble<String, Animation, Animation> processLocal)
    {
        this.parent = parent;
        this.reader = boneReader;
        this.animator = animator;
        this.controllerReader = controllerReader;
        this.refresh = refresh;
        this.boneType = boneType;
        this.processLocal = processLocal; 
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
                Animation animation = animator.animation(id);
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
                
                Animation transform = time -> parent.animate(time).mul(processLocal.call(name, local).animate(time));
                transform = transform.refreshable(refresh);

                if(type.equals(boneType))
                {
                    reader.read(name, transform);
                }
                
                child.children("node", new ColladaBoneNodeReader(boneType, transform, animator, refresh, reader, controllerReader, processLocal));
            })));
            child.children("instance_controller", controllerReader);
        }));
    }
}