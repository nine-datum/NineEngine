package nine.geometry;

import nine.function.RefreshStatus;
import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;

public interface Model
{
    Drawing instance(ShaderPlayer shader, RefreshStatus refreshStatus);
}