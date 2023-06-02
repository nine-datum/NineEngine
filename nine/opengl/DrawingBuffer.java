package nine.opengl;

import java.nio.FloatBuffer;

public interface DrawingBuffer
{
    DrawingAttributeBuffer attribute(int stride, FloatBuffer data);
}