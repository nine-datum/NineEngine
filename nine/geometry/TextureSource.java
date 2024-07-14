package nine.geometry;

import nine.opengl.OpenGL;
import nine.opengl.Texture;

public interface TextureSource
{
	Texture instance(OpenGL gl);
}