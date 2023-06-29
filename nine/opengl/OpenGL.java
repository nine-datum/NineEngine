package nine.opengl;

import nine.buffer.Buffer;

public interface OpenGL
{
    DrawingBuffer vao(Buffer<Integer> elements);
    ShaderCompiler compiler();

    Drawing depthOn(Drawing drawing);
    Drawing smooth(Drawing drawing);
    Drawing clockwise(Drawing drawing);
}