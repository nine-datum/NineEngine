package nine.game;

import nine.function.RefreshStatus;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.collada.ColladaAnimationParser;
import nine.geometry.collada.ColladaGeometryParser;
import nine.geometry.collada.ColladaMaterialParser;
import nine.geometry.collada.ColladaOpenGLGrahics;
import nine.geometry.collada.ColladaSkinParser;
import nine.io.Storage;
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
    static Graphics collada(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage,
        RefreshStatus refreshStatus,
        ColladaGeometryParser geometryParser,
	    ColladaSkinParser skinParser,
	    ColladaAnimationParser animationParser,
	    ColladaMaterialParser materialParser)
    {
        return new ColladaOpenGLGrahics(gl, diffuseShader, skinShader, storage, refreshStatus,
    		geometryParser,
    		skinParser,
    		animationParser,
    		materialParser);
    }
    
    default AnimatedSkeleton animation(String file)
    {
    	return animation(file, "JOINT");
    }
}