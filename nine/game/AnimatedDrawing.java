package nine.game;

import nine.geometry.Skeleton;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public interface AnimatedDrawing
{
	Drawing animate(Matrix4f projection, Vector3f worldLight, Matrix4f root, Skeleton animation, Skeleton objectsAnimation);
}