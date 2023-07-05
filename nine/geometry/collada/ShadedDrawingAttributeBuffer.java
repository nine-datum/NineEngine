package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.ShaderPlayer;

public class ShadedDrawingAttributeBuffer implements DrawingAttributeBuffer
{
    DrawingAttributeBuffer base;
    ShaderPlayer shader;

    public ShadedDrawingAttributeBuffer(DrawingAttributeBuffer base, ShaderPlayer shader)
    {
        this.base = base;
        this.shader = shader;
    }

    @Override
    public DrawingAttributeBuffer attribute(int stride, Buffer<Float> data)
    {
        return new ShadedDrawingAttributeBuffer(base.attribute(stride, data), shader);
    }

    @Override
    public Drawing drawing()
    {
        return shader.play(base.drawing());
    }
}