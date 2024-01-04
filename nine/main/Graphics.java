package nine.main;

import nine.function.RefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.io.Storage;
import nine.opengl.OpenGL;
import nine.opengl.Shader;

public interface Graphics
{
	AnimatedSkeleton animation(String file);
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
}