package nine.main;

import java.io.File;

import nine.function.RefreshStatus;
import nine.game.AnimatedDrawing;
import nine.game.Graphics;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.collada.ColladaModel;
import nine.geometry.collada.ColladaNode;
import nine.geometry.collada.ColladaSkinnedModel;
import nine.io.Storage;
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
        var modelSource = new ColladaSkinnedModel(ColladaNode.fromFile(storage.open(file))).load(gl, textureStorage);
        var shaderPlayer = skinShader.player();
        var staticShaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var combinedUniforms = Uniforms.many(uniforms, staticShaderPlayer.uniforms());
        var lightUniform = combinedUniforms.uniformVector("worldLight");
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
            });
            return gl.depthOn(gl.smooth(drawing));
        };
    }

    @Override
    public TransformedDrawing model(String file)
    {
        var textureStorage = this.storage.relative(new File(file).getParent());
        var modelSource = new ColladaModel(ColladaNode.fromFile(storage.open(file))).load(gl, textureStorage);
        var shaderPlayer = diffuseShader.player();
        var uniforms = shaderPlayer.uniforms();
        var lightUniform = uniforms.uniformVector("worldLight");
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
                shadedModel.draw();
            };
            return shaderPlayer.play(gl.depthOn(gl.smooth(initializedDrawing)));
        };
    }
}