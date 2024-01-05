package nine.geometry.collada;

import java.util.ArrayList;
import java.util.List;

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
        List<Drawing> drawings = new ArrayList<Drawing>();

        materialParser.read(node, materials ->
        geometryParser.read(node, (source, material, floatBuffers, intBuffers) ->
        {
            DrawingAttributeBuffer buffer = new TexturedDrawingAttributeBuffer(
                gl.texture(storage.open(materials.textureFile(material))),
                gl.vao(intBuffers.map("INDEX"))
                    .attribute(3, floatBuffers.map("VERTEX"))
                    .attribute(2, floatBuffers.map("TEXCOORD"))
                    .attribute(3, floatBuffers.map("NORMAL")));
            drawings.add(buffer.drawing());
        }));

        return shader -> shader.play(new CompositeDrawing(new IterableFlow<Drawing>(drawings)));
    }
}
