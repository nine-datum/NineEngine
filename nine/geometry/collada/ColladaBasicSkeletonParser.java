package nine.geometry.collada;

import java.util.HashMap;

import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;

public class ColladaBasicSkeletonParser implements ColladaSkeletonParser
{
    @Override
    public void read(ColladaNode node, SkeletonReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_visual_scenes", scenes ->
        scenes.children("visual_scene", scene ->
        {
            HashMap<String, Matrix4f> bones = new HashMap<>();
            scene.children("node", new ColladaBoneNodeReader(Matrix4fIdentity.identity, bones::put, controller ->
            controller.attribute("url", skinId ->
            {
                reader.read(skinId, bones::get);
            })));
        })));
    }
}