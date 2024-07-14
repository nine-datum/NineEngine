package nine.geometry;

import nine.opengl.ShaderPlayer;

public interface Model
{
    MaterializedDrawing instance(ShaderPlayer shader);
}