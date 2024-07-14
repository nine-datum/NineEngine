package nine.geometry;

import nine.opengl.OpenGL;

public interface MaterialSource
{
	Material instance(OpenGL gl);
}