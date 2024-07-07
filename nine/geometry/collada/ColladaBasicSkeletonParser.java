package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nine.function.Condition;
import nine.function.FunctionDouble;
import nine.function.RefreshStatus;
import nine.geometry.Animation;
import nine.geometry.Animator;
import nine.geometry.MapSkeleton;

public class ColladaBasicSkeletonParser implements ColladaSkeletonParser
{
	Condition<String> boneType;
	FunctionDouble<String, Animation, Animation> processLocal;
	
	public ColladaBasicSkeletonParser(Condition<String> boneType)
	{
		this.boneType = boneType;
		this.processLocal = (b, a) -> a;
	}
	public ColladaBasicSkeletonParser(Condition<String> boneType, FunctionDouble<String, Animation, Animation> processLocal)
	{
		this.boneType = boneType;
		this.processLocal = processLocal;
	}
	
    @Override
    public void read(ColladaNode node, Animator animator, RefreshStatus refresh, SkeletonReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_visual_scenes", scenes ->
        scenes.children("visual_scene", scene ->
        {
            List<ColladaNode> controllers = new ArrayList<>();
            HashMap<String, Animation> bones = new HashMap<>();
            scene.children("node", new ColladaBoneNodeReader(boneType, Animation.none, animator, refresh, bones::put, controllers::add, processLocal));
            for(ColladaNode controller : controllers)
            {
                controller.attribute("url", skinId ->
                {
                    reader.read(skinId, new MapSkeleton(bones));
                });
            }
        })));
    }
}