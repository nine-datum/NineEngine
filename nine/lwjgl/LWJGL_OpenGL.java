package nine.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import nine.buffer.Buffer;
import nine.function.ErrorPrinter;
import nine.io.InputStreamFromFlow;
import nine.io.StorageResource;
import nine.opengl.Drawing;
import nine.opengl.DrawingBuffer;
import nine.opengl.OpenGL;
import nine.opengl.Profiler;
import nine.opengl.ShaderCompiler;
import nine.opengl.Texture;

import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.util.Hashtable;

public class LWJGL_OpenGL implements OpenGL
{
    static ColorModel rgbaModel;
    static ColorModel rgbModel;
    final Texture blank;
    Profiler profiler;

    public LWJGL_OpenGL()
    {
        blank = Texture.blank(this);
        profiler = Profiler.none;
    }
    public LWJGL_OpenGL(Profiler profiler)
    {
    	this();
    	this.profiler = profiler;
    }

    static
    {
        rgbaModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                            new int[] {8,8,8,8},
                                            true,
                                            false,
                                            ComponentColorModel.TRANSLUCENT,
                                            DataBuffer.TYPE_BYTE);

        rgbModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                            new int[] {8,8,8,0},
                                            false,
                                            false,
                                            ComponentColorModel.OPAQUE,
                                            DataBuffer.TYPE_BYTE);
    }

    @Override
    public DrawingBuffer vao(Buffer<Integer> elements)
    {
        return new LWJGL_DrawingBuffer(elements);
    }

    @Override
    public ShaderCompiler compiler()
    {
        return new LWJGL_ShaderCompiler();
    }

    @Override
    public Drawing depthOn(Drawing drawing)
    {
        return () ->
        {
            GL20.glEnable(GL20.GL_DEPTH_TEST);
            drawing.draw();
            GL20.glDisable(GL20.GL_DEPTH_TEST);
        };
    }
    @Override
    public Drawing smooth(Drawing drawing)
    {
        return drawing;
    }
    @Override
    public Drawing clockwise(Drawing drawing)
    {
        return () ->
        {
            GL20.glFrontFace(GL20.GL_CW);
            drawing.draw();
            GL20.glFrontFace(GL20.GL_CCW);
        };
    }

    @Override
    public Texture texture(StorageResource input, boolean mipmaps)
    {
        Texture[] texture = { null };

        input.read(flow ->
        {
            BufferedImage image;
            try
            {
                image = ImageIO.read(new InputStreamFromFlow(flow));
            }
            catch(Throwable th)
            {
                ErrorPrinter.instance.call(th);
                image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, 255);
            }
            texture[0] = texture(image, mipmaps);
        }, ErrorPrinter.instance);

        return texture[0];
    }
    public Texture texture(BufferedImage image, boolean mipmaps)
    {
        BufferedImage actualImage;
        ColorModel model;
        int bands;

        if (image.getColorModel().hasAlpha())
        {
            bands = 4;
            model = rgbaModel;
        }
        else
        {
            bands = 3;
            model = rgbModel;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, bands, null);
        int[] pixel = new int[4];
        var imageRaster = image.getRaster();
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                imageRaster.getPixel(x, y, pixel);
                raster.setPixel(x, height - y - 1, pixel);
            }
        }
        actualImage = new BufferedImage(model,raster,false,new Hashtable<>());

        byte[] data = ((DataBufferByte)actualImage.getRaster().getDataBuffer()).getData();
        return texture(data, width, height, mipmaps);
    }
    @Override
    public Texture texture(byte[] data, int width, int height, boolean mipmaps) {
        int id = GL11.glGenTextures();
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(data);
        buffer.flip();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
  	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mipmaps ? GL11.GL_LINEAR_MIPMAP_NEAREST : GL11.GL_LINEAR);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            width,
            height,
            0,
            GL15.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            buffer);
        if(mipmaps) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

    		return new Texture()
        {
            @Override
            public Drawing apply(Drawing drawing)
            {
                return () ->
                {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                    drawing.draw();
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                };
            }

            @Override
            public void dispose()
            {
                GL11.glDeleteTextures(id);
            }
        };
    }
    @Override
    public Texture blankTexture()
    {
        return this.blank;
    }
	@Override
	public Profiler profiler()
	{
		return profiler;
	}
    @Override
    public void clearDepth()
    {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }
    @Override
    public void clearColor(float r, float g, float b, float a)
    {
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }
}
