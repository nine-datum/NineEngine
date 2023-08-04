package nine.geometry;

import nine.opengl.OpenGL;

public interface ModelAsset
{
    Model load(OpenGL gl);
}