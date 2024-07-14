package nine.geometry.collada;

import java.util.ArrayList;
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

    public ColladaModel(ColladaNode node, ColladaGeometryParser geometryParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
    }
    public ColladaModel(ColladaNode node)
    {
        this(node, new ColladaBasicGeometryParser());
    }

    @Override
    public Model load(OpenGL gl)
    {
        List<Model> models = new ArrayList<>();
        
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            DrawingAttributeBuffer buffer = gl.vao(intBuffers.map("INDEX"))
	            .attribute(3, floatBuffers.map("VERTEX").fromRightToLeftHanded())
	            .attribute(2, floatBuffers.map("TEXCOORD"))
	            .attribute(3, floatBuffers.map("NORMAL").fromRightToLeftHanded());
            var bufferDrawing = buffer.drawing();
            models.add(shader -> materials ->
            {
            	return materials.material(material).apply(shader, bufferDrawing);
            });
        });

        return shader -> materials -> shader.play(Drawing.of(Flow.iterable(models.stream().map(m -> m.instance(shader).materialize(materials)).toList())));
    }
}
