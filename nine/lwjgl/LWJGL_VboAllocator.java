package nine.lwjgl;

import nine.function.Action;
import nine.opengl.Drawing;

public interface LWJGL_VboAllocator
{
    LWJGL_Vbo vbo(int index);
    Drawing drawing(Action activation);
}