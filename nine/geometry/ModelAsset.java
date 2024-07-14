package nine.geometry;

import nine.io.Storage;
import nine.opengl.OpenGL;

public interface ModelAsset
{
    Model load(OpenGL gl);
}