package nine.geometry;

import nine.drawing.Color;

public class Material
{
	public final String textureFile;
	public final Color color;
	
	public Material(String textureFile, Color color)
	{
		this.textureFile = textureFile;
		this.color = color;
	}
}