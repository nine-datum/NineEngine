package nine.main;

import nine.geometry.collada.Skeleton;
import nine.math.Matrix4f;
import nine.opengl.Drawing;

public interface AnimatedDrawing
{
	Drawing animate(Matrix4f root, Skeleton animation);
}