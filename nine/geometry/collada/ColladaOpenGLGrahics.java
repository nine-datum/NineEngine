package nine.geometry.collada;

import java.io.File;

import nine.drawing.ColorFloatStruct;
import nine.function.RefreshStatus;
import nine.game.AnimatedDrawing;
import nine.game.Graphics;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.assimp.AssimpGraphics;
import nine.io.Storage;
import nine.main.TransformedDrawing;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.Uniforms;

public class ColladaOpenGLGrahics implements Graphics
{
    OpenGL gl;
    Shader diffuseShader;
    Shader skinShader;
    Storage storage;
    RefreshStatus refreshStatus;
    
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaAnimationParser animationParser;
    ColladaMaterialParser materialParser;

    public ColladaOpenGLGrahics(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage,
        RefreshStatus refreshStatus)
    {
        this.gl = gl;
        this.diffuseShader = diffuseShader;
        this.skinShader = skinShader;
        this.storage = storage;
        this.refreshStatus = refreshStatus;
        
        this.geometryParser = new ColladaBasicGeometryParser();
        this.skinParser = new ColladaBasicSkinParser();
        this.animationParser = new ColladaBasicAnimationParser();
        this.materialParser = new ColladaBasicMaterialParser();
    }
    
    public ColladaOpenGLGrahics(
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
        this.gl = gl;
        this.diffuseShader = diffuseShader;
        this.skinShader = skinShader;
        this.storage = storage;
        this.refreshStatus = refreshStatus;
        
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.animationParser = animationParser;
        this.materialParser = materialParser;
    }

    @Override
    public AnimatedSkeleton animation(String file, String boneType)
    {
        return AnimatedSkeleton.fromCollada(ColladaNode.fromFile(storage.open(file)), boneType, refreshStatus);
    }

    @Override
    public AnimatedDrawing animatedModel(String file)
    {
        var textureStorage = this.storage.relative(new File(file).getParent());
        var modelSource = new ColladaSkinnedModel(
        		ColladaNode.fromFile(storage.open(file)),
        		geometryParser,
        		skinParser,
        		animationParser,
        		materialParser
    		)
    		.load(gl, textureStorage);
        var shaderPlayer = skinShader.player();
        var staticShaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var combinedUniforms = Uniforms.many(uniforms, staticShaderPlayer.uniforms());
        var lightUniform = combinedUniforms.uniformVector("worldLight");
        var colorUniform = uniforms.uniformColor("color");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = combinedUniforms.uniformMatrix("projection");
        var shadedModel = modelSource.shade(shaderPlayer, staticShaderPlayer);
        return (projection, light, transform, animation, objectsAnimation) ->
        {
            var drawing = shadedModel.instance(animation, objectsAnimation, () ->
            {
            	lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
                colorUniform.load(new ColorFloatStruct(1, 1, 1, 1));
            });
            return gl.depthOn(gl.smooth(drawing));
        };
    }

    @Override
    public TransformedDrawing model(String file)
    {
//    	if(true)
//    	{
//    		return new AssimpGraphics(gl, skinShader, diffuseShader, storage, refreshStatus).model(file);
//    	}
        var textureStorage = this.storage.relative(new File(file).getParent());
        var modelSource = new ColladaModel(
        		ColladaNode.fromFile(storage.open(file)),
        		geometryParser,
        		materialParser
    		)
    		.load(gl, textureStorage);
        var shaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var lightUniform = uniforms.uniformVector("worldLight");
        var colorUniform = uniforms.uniformColor("color");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = uniforms.uniformMatrix("projection");
        var shadedModel = modelSource.instance(shaderPlayer);
        return (projection, light, transform) ->
        {
            Drawing initializedDrawing = () ->
            {
                lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
                colorUniform.load(new ColorFloatStruct(1, 1, 1, 1));
                shadedModel.draw();
            };
            return shaderPlayer.play(gl.depthOn(gl.smooth(initializedDrawing)));
        };
    }
}