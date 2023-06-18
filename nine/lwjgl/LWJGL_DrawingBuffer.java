package nine.lwjgl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import nine.function.Action;
import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.DrawingBuffer;

public class LWJGL_DrawingBuffer implements DrawingBuffer
{
    int[] elements;

    LWJGL_DrawingBuffer(int[] elements)
    {
        this.elements = elements;
    }

    @Override
    public DrawingAttributeBuffer attribute(int stride, float[] data)
    {
        LWJGL_Vao vao = new LWJGL_Vao()
        {
            @Override
            public LWJGL_VboAllocator allocate(int count)
            {
                int vao = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(vao);

                int[] buffers = new int[count + 1];
                GL20.glGenBuffers(buffers);

                GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
                GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elements, GL20.GL_STATIC_DRAW);

                return new LWJGL_VboAllocator()
                {
                    @Override
                    public LWJGL_Vbo vbo(int index)
                    {
                        return action ->
                        {
                            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, buffers[index + 1]);
                            action.call();
                        };
                    }

                    @Override
                    public Drawing drawing(Action activation)
                    {
                        return () ->
                        {
                            GL20.glEnable(GL20.GL_CULL_FACE);
                            GL30.glBindVertexArray(vao);
                            activation.call();
                            GL30.glDrawElements(GL30.GL_TRIANGLES, elements.length, GL30.GL_UNSIGNED_INT, 0);
                            GL30.glBindVertexArray(0);
                        };
                    }
                };
            }
        };
        return new LWJGL_DrawingAttributeBuffer(vao, 0, stride, data);
    }
}