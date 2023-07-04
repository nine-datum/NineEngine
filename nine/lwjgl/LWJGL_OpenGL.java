package nine.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import nine.buffer.Buffer;
import nine.function.ErrorPrinter;
import nine.io.StorageResource;
import nine.opengl.Drawing;
import nine.opengl.DrawingBuffer;
import nine.opengl.OpenGL;
import nine.opengl.ShaderCompiler;
import nine.opengl.Texture;

import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.Graphics;
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
        return () ->
        {
            GL20.glShadeModel(GL20.GL_SMOOTH);
            drawing.draw();
            GL20.glShadeModel(GL20.GL_FLAT);
        };
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
    public Texture texture(StorageResource input)
    {
        int id = GL11.glGenTextures();

        input.read(flow ->
        {
            BufferedImage image;
            try
            {
                image = ImageIO.read(flow);
            }
            catch(Throwable th)
            {
                ErrorPrinter.instance.call(th);
                image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, 255);
            }

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
            actualImage = new BufferedImage(model,raster,false,new Hashtable<>());
    
            Graphics2D g = (Graphics2D)actualImage.getGraphics();
            g.setColor(new Color(0f,0f,0f,0f));
            g.fillRect(0, 0, width, height);
            g.translate(0, height);
            g.scale(1f, -1f);
            g.drawImage(image,0,0,null);
            image = actualImage;
        
            byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(data);
            buffer.flip();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                image.getWidth(),
                image.getHeight(),
                0,
                image.getColorModel().hasAlpha() ? GL15.GL_RGBA : GL15.GL_RGB,
                GL11.GL_UNSIGNED_BYTE,
                buffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        }, ErrorPrinter.instance);

		return drawing -> () ->
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
		    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            drawing.draw();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        };
    }
}