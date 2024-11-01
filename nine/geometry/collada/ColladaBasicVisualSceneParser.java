package nine.geometry.collada;

import nine.buffer.TextValueBuffer;
import nine.math.Matrix4f;

public class ColladaBasicVisualSceneParser implements ColladaVisualSceneParser
{
    class ColladaVisualSceneNodeReader implements NodeReader
    {
        public ColladaVisualSceneNodeReader(Matrix4f parent, ColladaVisualSceneReader reader) {
          this.parent = parent;
          this.reader = reader;
        }

        Matrix4f parent;
        ColladaVisualSceneReader reader;

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
                    Matrix4f contentMatrix = Matrix4f.from_COLLADA_Buffer(new TextValueBuffer<>(content, Float::parseFloat), 0);
                    Matrix4f root = parent.mul(contentMatrix);
                    child.children("instance_geometry",
                    geom -> geom.attribute("url",
                    url -> reader.read(url, root)));
                    child.children("node", new ColladaVisualSceneNodeReader(root, reader));
                })));
            }));
        }
    }

    @Override
    public void read(ColladaNode node, ColladaVisualSceneReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_visual_scenes", scenes ->
        scenes.children("visual_scene", scene ->
        {
            scene.children("node", new ColladaVisualSceneNodeReader(Matrix4f.identity, reader));
        })));
    }
}