package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.Texture;

public class TexturedDrawingAttributeBuffer implements DrawingAttributeBuffer
{
    Texture texture;
    DrawingAttributeBuffer source;

    public TexturedDrawingAttributeBuffer(Texture texture, DrawingAttributeBuffer source) {
        this.texture = texture;
        this.source = source;
    }

    @Override
    public DrawingAttributeBuffer attribute(int stride, Buffer<Float> data)
    {
        return new TexturedDrawingAttributeBuffer(texture, source.attribute(stride, data));
    }

    @Override
    public Drawing drawing()
    {
        return texture.apply(source.drawing());
    }
}