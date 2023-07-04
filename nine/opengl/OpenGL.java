package nine.opengl;

import nine.buffer.Buffer;
import nine.io.StorageResource;

public interface OpenGL
{
    DrawingBuffer vao(Buffer<Integer> elements);
    ShaderCompiler compiler();
    Texture texture(StorageResource input);

    Drawing depthOn(Drawing drawing);
    Drawing smooth(Drawing drawing);
    Drawing clockwise(Drawing drawing);
}