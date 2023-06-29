package nine.opengl;

import nine.buffer.Buffer;

public interface DrawingBuffer
{
    DrawingAttributeBuffer attribute(int stride, Buffer<Float> data);
}