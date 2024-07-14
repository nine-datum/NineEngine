package nine.geometry;

import nine.drawing.Color;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.ShaderPlayer;
import nine.opengl.Texture;

public interface Material
{
	Drawing apply(ShaderPlayer shader, Drawing drawing);
	
	static Material blank(OpenGL gl)
	{
		return Material.textureAndColor(Texture.blank(gl), Color.floats(1, 0, 1, 1));
	}
	
	static Material textureAndColor(Texture texture, Color color)
	{
		return (shader, drawing) ->
		{
			var colorUniform = shader.uniforms().uniformColor("color");
			var textured = texture.apply(drawing);
			return () ->
			{
				colorUniform.load(color);
				textured.draw();
			};
		};
	}
}