package nine.geometry;

import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;

public interface Model
{
    Drawing instance(ShaderPlayer shader);
}