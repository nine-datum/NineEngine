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
    ColladaMaterialParser materialParser;

    public ColladaModel(ColladaNode node, ColladaGeometryParser geometryParser, ColladaMaterialParser materialParser)
    {
        this.node = node;
        this.geometryParser = geometryParser;
        this.materialParser = materialParser;
    }
    public ColladaModel(ColladaNode node)
    {
        this(node, new ColladaBasicGeometryParser(), new ColladaBasicMaterialParser());
    }

    @Override
    public Model load(OpenGL gl, Storage storage)
    {
        List<Model> models = new ArrayList<>();

        materialParser.read(node, materials ->
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
        	var mat = materials.properties(material);
            DrawingAttributeBuffer buffer = new TexturedDrawingAttributeBuffer(
                gl.texture(storage.open(mat.textureFile)),
                gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX").fromRightToLeftHanded())
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL").fromRightToLeftHanded()));
            var drawing = buffer.drawing();
            models.add(shader ->
            {
            	var colorUniform = shader.uniforms().uniformColor("color");
            	return () ->
            	{
            		colorUniform.load(mat.color);
            		drawing.draw();
            	};
            });
        }));

        return shader -> shader.play(Drawing.of(Flow.iterable(models.stream().map(m -> m.instance(shader)).toList())));
    }
}
