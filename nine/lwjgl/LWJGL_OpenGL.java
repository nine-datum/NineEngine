package nine.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import nine.buffer.Buffer;
import nine.function.ErrorPrinter;
import nine.io.InputStreamFromFlow;
import nine.io.StorageResource;
import nine.opengl.Drawing;
import nine.opengl.DrawingBuffer;
import nine.opengl.OpenGL;
import nine.opengl.ShaderCompiler;
import nine.opengl.Texture;

import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;

public class LWJGL_OpenGL implements OpenGL
{
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
                image = ImageIO.read(new InputStreamFromFlow(flow));
            }
            catch(Throwable th)
            {
                ErrorPrinter.instance.call(th);
                image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, 255);
            }
        
            byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(data);
            buffer.flip();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                image.getWidth(),
                image.getHeight(),
                0,
                image.getColorModel().hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE,
                buffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        }, ErrorPrinter.instance);

		return drawing -> () ->
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
		    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            drawing.draw();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        };
    }
}