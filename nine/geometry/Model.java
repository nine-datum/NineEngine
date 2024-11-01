package nine.geometry;

import nine.math.Matrix4f;
import nine.opengl.ShaderPlayer;
import nine.opengl.Uniform;

public interface Model
{
    MaterializedDrawing instance(Uniform<Matrix4f> transform, ShaderPlayer shader);
}