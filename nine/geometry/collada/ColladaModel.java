package nine.geometry.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nine.collection.Flow;
import nine.collection.IterableFlow;
import nine.geometry.Model;
import nine.geometry.ModelAsset;
import nine.io.Storage;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;

public class ColladaModel implements ModelAsset
{
    ColladaNode node;
    ColladaGeometryParser geometryParser;
    ColladaVisualSceneParser sceneParser;

    public ColladaModel(ColladaNode node, ColladaGeometryParser geometryParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.sceneParser = new ColladaBasicVisualSceneParser();
    }
    public ColladaModel(ColladaNode node, ColladaGeometryParser geometryParser, ColladaVisualSceneParser sceneParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.sceneParser = sceneParser;
    }
    public ColladaModel(ColladaNode node)
    {
        this(node, new ColladaBasicGeometryParser(), new ColladaBasicVisualSceneParser());
    }

    @Override
    public Model load(OpenGL gl)
    {
        HashMap<String, Model> models = new HashMap<>();
        
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            DrawingAttributeBuffer buffer = gl.vao(intBuffers.map("INDEX"))
	            .attribute(3, floatBuffers.map("VERTEX").fromRightToLeftHanded())
	            .attribute(2, floatBuffers.map("TEXCOORD"))
	            .attribute(3, floatBuffers.map("NORMAL").fromRightToLeftHanded());
            var bufferDrawing = buffer.drawing();
            models.put("#" + source, (transform, shader) -> materials ->
            {
            	return materials.material(material).apply(shader, bufferDrawing);
            });
        });
        List<Model> sceneModels = new ArrayList<>();
        sceneParser.read(node, (id, root) ->
        {
            Model model = models.get(id);
            if(model != null)
            {
                sceneModels.add((transform, shader) -> materials ->
                {
                    Drawing transformLoad = () -> transform.load(root);
                    return Drawing.of(transformLoad, model.instance(transform, shader).materialize(materials));
                });
            }
        });

        return (transform, shader) -> materials ->
        {
            var drawings = Drawing.of(Flow.iterable(sceneModels.stream().map(m ->
            {
                return m.instance(transform, shader).materialize(materials);
            }).toList()));
            return shader.play(drawings);
        };
    }
}
