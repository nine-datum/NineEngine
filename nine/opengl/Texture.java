package nine.opengl;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface Texture
{
    Drawing apply(Drawing drawing);
    void dispose();

    static Texture blank(OpenGL gl)
    {
      byte[] data = new byte[] { -1, -1, -1, -1 };
    	return gl.texture(data, 1, 1, false, false);
    }
}
