package nine.lwjgl;

import nine.opengl.DisposableDrawing;

public interface LWJGL_VboAllocator
{
    LWJGL_Vbo vbo(int index);
    DisposableDrawing drawing(LWJGL_VboActivation activation);
}