package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nine.function.Condition;
import nine.geometry.Animation;
import nine.geometry.AnimationSource;
import nine.geometry.Animator;
import nine.geometry.MapSkeleton;

public class ColladaBasicSkeletonParser implements ColladaSkeletonParser
{
	Condition<String> boneType;
	
	public ColladaBasicSkeletonParser(Condition<String> boneType)
	{
		this.boneType = boneType;
	}
	
    @Override
    public void read(ColladaNode node, Animator animator, SkeletonReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_visual_scenes", scenes ->
        scenes.children("visual_scene", scene ->
        {
            List<ColladaNode> controllers = new ArrayList<>();
            HashMap<String, AnimationSource> bones = new HashMap<>();
            scene.children("node", new ColladaBoneNodeReader(boneType, r -> Animation.none, animator, bones::put, controllers::add));
            for(ColladaNode controller : controllers)
            {
                controller.attribute("url", skinId ->
                {
                    reader.read(skinId, bones.keySet(), refresh -> new MapSkeleton(Map.ofEntries(
                		bones
                			.entrySet()
                			.stream()
                			.map(e -> Map.entry(e.getKey(), e.getValue().instance(refresh)))
                			.<Map.Entry<String, Animation>>toArray(Map.Entry[]::new))));
                });
            }
        })));
    }
}