package nine.geometry;

import nine.math.ValueFloat;
import nine.opengl.Drawing;

public interface AnimatedDrawing
{
    Drawing animate(ValueFloat time);
}