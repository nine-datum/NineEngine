package nine.geometry;

import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.ShaderPlayer;

public interface Model
{
    Drawing load(OpenGL gl, ShaderPlayer shader);
}
