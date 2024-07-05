package nine.game;

import nine.function.RefreshStatus;
import nine.geometry.AnimatedSkeleton;
import nine.io.Storage;
import nine.main.ColladaOpenGLGrahics;
import nine.main.TransformedDrawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;

public interface Graphics
{
	AnimatedSkeleton animation(String file, String boneType);
    TransformedDrawing model(String file);
	AnimatedDrawing animatedModel(String file);

    static Graphics collada(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage,
        RefreshStatus refreshStatus)
    {
        return new ColladaOpenGLGrahics(gl, diffuseShader, skinShader, storage, refreshStatus);
    }
    
    default AnimatedSkeleton animation(String file)
    {
    	return animation(file, "JOINT");
    }
}