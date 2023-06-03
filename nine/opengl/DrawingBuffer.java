package nine.opengl;

public interface DrawingBuffer
{
    DrawingAttributeBuffer attribute(int stride, float[] data);
}