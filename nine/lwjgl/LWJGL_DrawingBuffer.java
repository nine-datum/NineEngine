package nine.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import nine.function.Action;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.DrawingBuffer;

public class LWJGL_DrawingBuffer implements DrawingBuffer
{
    IntBuffer elements;

    LWJGL_DrawingBuffer(IntBuffer elements)
    {
        this.elements = elements;
    }

    @Override
    public DrawingAttributeBuffer attribute(int stride, FloatBuffer data)
    {
        LWJGL_Vao vao = new LWJGL_Vao()
        {
            @Override
            public LWJGL_VboAllocator allocate(int count)
            {
                IntBuffer buffers = IntBuffer.allocate(count + 1);
                int vao = buffers.get(0);

                return new LWJGL_VboAllocator()
                {
                    @Override
                    public LWJGL_Vbo vbo(int index)
                    {
                        return action ->
                        {
                            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, buffers.get(index + 1));
                            action.call();
                        };
                    }

                    @Override
                    public Drawing drawing(Action activation)
                    {
                        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, vao);
                        GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elements, GL20.GL_STATIC_DRAW);
                        return () ->
                        {
                            GL20.glDisable(GL20.GL_CULL_FACE);
                            GL30.glBindVertexArray(vao);
                            activation.call();
                            GL30.glDrawElements(GL30.GL_TRIANGLES, elements.capacity(), GL30.GL_UNSIGNED_INT, 0);
                            GL30.glBindVertexArray(0);
                        };
                    }
                };
            }
        };
        return new LWJGL_DrawingAttributeBuffer(vao, 0, stride, data);
    }
}