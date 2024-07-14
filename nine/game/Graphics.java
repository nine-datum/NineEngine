package nine.game;

import nine.function.Condition;
import nine.geometry.AnimatedSkeletonSource;
import nine.geometry.MaterialProvider;
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
	AnimatedSkeletonSource animation(String file, Condition<String> boneType);
    TransformedDrawing model(String file);
	AnimatedDrawing animatedModel(String file);
	MaterialProvider materials(String file);

    static Graphics collada(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage)
    {
        return new ColladaOpenGLGrahics(gl, diffuseShader, skinShader, storage);
    }
    static Graphics collada(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage,
        ColladaGeometryParser geometryParser,
	    ColladaSkinParser skinParser,
	    ColladaAnimationParser animationParser,
	    ColladaMaterialParser materialParser)
    {
        return new ColladaOpenGLGrahics(gl, diffuseShader, skinShader, storage,
    		geometryParser,
    		skinParser,
    		animationParser,
    		materialParser);
    }
    
    default AnimatedSkeletonSource animation(String file)
    {
    	return animation(file, Condition.equality("JOINT"));
    }
}