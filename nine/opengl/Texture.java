package nine.opengl;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface Texture
{
    Drawing apply(Drawing drawing);
    void dispose();
    
    static Texture blank(OpenGL gl)
    {
    	var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    	img.setRGB(0, 0, Color.WHITE.getRGB());
    	return gl.texture(img);
    }
}