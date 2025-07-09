package nine.opengl;

import nine.buffer.Buffer;
import nine.io.StorageResource;

public interface OpenGL
{
    DrawingBuffer vao(Buffer<Integer> elements);
    ShaderCompiler compiler();
    Texture texture(StorageResource input, boolean mipmaps);
    Texture texture(byte[] data, int width, int height, boolean alpha, boolean mipmaps);
    Texture blankTexture();

    Drawing depthOn(Drawing drawing);
    Drawing smooth(Drawing drawing);
    Drawing clockwise(Drawing drawing);

    void clearDepth();
    void clearColor(float r, float g, float b, float a);

    Profiler profiler();

    default Texture texture(StorageResource input)
    {
        return texture(input, true);
    }
    default Texture texture(byte[] data, int width, int height, boolean alpha)
    {
        return texture(data, width, height, alpha, true);
    }
}
