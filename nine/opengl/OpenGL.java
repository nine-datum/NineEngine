package nine.opengl;

import java.awt.image.BufferedImage;

import nine.buffer.Buffer;
import nine.io.StorageResource;

public interface OpenGL
{
    DrawingBuffer vao(Buffer<Integer> elements);
    ShaderCompiler compiler();
    Texture texture(StorageResource input, boolean mipmaps);
    Texture texture(BufferedImage image, boolean mipmaps);
    Texture blankTexture();

    Drawing depthOn(Drawing drawing);
    Drawing smooth(Drawing drawing);
    Drawing clockwise(Drawing drawing);

    Profiler profiler();

    default Texture texture(StorageResource input)
    {
        return texture(input, true);
    }
    default Texture texture(BufferedImage image)
    {
        return texture(image, true);
    }
}