package nine.geometry.collada;

import java.nio.file.Path;
import java.util.HashMap;

import nine.function.Condition;
import nine.game.AnimatedDrawing;
import nine.game.Graphics;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.AnimatedSkeletonSource;
import nine.geometry.Material;
import nine.geometry.MaterialProvider;
import nine.io.Storage;
import nine.main.TransformedDrawing;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.Texture;
import nine.opengl.Uniforms;

public class ColladaOpenGLGrahics implements Graphics
{
    OpenGL gl;
    Shader diffuseShader;
    Shader skinShader;
    Storage storage;
    
    ColladaGeometryParser geometryParser;
    ColladaSkinParser skinParser;
    ColladaAnimationParser animationParser;
    ColladaMaterialParser materialParser;

    public ColladaOpenGLGrahics(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Storage storage)
    {
        this.gl = gl;
        this.diffuseShader = diffuseShader;
        this.skinShader = skinShader;
        this.storage = storage;
        
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
        ColladaGeometryParser geometryParser,
	    ColladaSkinParser skinParser,
	    ColladaAnimationParser animationParser,
	    ColladaMaterialParser materialParser)
    {
        this.gl = gl;
        this.diffuseShader = diffuseShader;
        this.skinShader = skinShader;
        this.storage = storage;
        
        this.geometryParser = geometryParser;
        this.skinParser = skinParser;
        this.animationParser = animationParser;
        this.materialParser = materialParser;
    }

    @Override
    public AnimatedSkeletonSource animation(String file, Condition<String> boneType)
    {
        return AnimatedSkeleton.fromCollada(ColladaNode.fromFile(storage.open(file)), boneType);
    }

    @Override
    public AnimatedDrawing animatedModel(String file)
    {
        var modelSource = new ColladaSkinnedModel(
        		ColladaNode.fromFile(storage.open(file)),
        		geometryParser,
        		skinParser,
        		materialParser
    		)
    		.load(gl);
        var shaderPlayer = skinShader.player();
        var staticShaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var combinedUniforms = Uniforms.many(uniforms, staticShaderPlayer.uniforms());
        var lightUniform = combinedUniforms.uniformVector("worldLight");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = combinedUniforms.uniformMatrix("projection");
        var shadedModel = modelSource.shade(shaderPlayer, staticShaderPlayer);
        return (projection, light, transform, animation, objectsAnimation, materials) ->
        {
            var drawing = shadedModel.instance(animation, objectsAnimation, transform, materials, () ->
            {
            	lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
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
        var modelSource = new ColladaModel(
        		ColladaNode.fromFile(storage.open(file)),
        		geometryParser
    		)
    		.load(gl);
        var shaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var lightUniform = uniforms.uniformVector("worldLight");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = uniforms.uniformMatrix("projection");
        var shadedModel = modelSource.instance(shaderPlayer);
        return (projection, light, transform, materials) ->
        {
        	var mmodel = shadedModel.materialize(materials);
            Drawing initializedDrawing = () ->
            {
                lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
                mmodel.draw();
            };
            return shaderPlayer.play(gl.depthOn(gl.smooth(initializedDrawing)));
        };
    }

	@Override
	public MaterialProvider materials(String file)
	{
		var dir = Path.of(file).getParent().toString();
		var node = ColladaNode.fromFile(storage.open(file));
		var materialsMap = new HashMap<String, Material>();
		new ColladaBasicMaterialParser().read(node, (name, texture, color) ->
		{
			Texture tex;
			if(texture == null)
			{
				tex = Texture.blank(gl);
			}
			else
			{
				tex = gl.texture(storage.open(Path.of(dir, texture).toString()));
			}
			materialsMap.put(name, Material.textureAndColor(tex, color));
		});
		return MaterialProvider.ofMap(materialsMap);			
	}
}