package nine.main;

import java.io.File;

import nine.function.RefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.geometry.collada.ColladaNode;
import nine.geometry.collada.ColladaSkinnedModel;
import nine.io.Storage;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;

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
    public AnimatedSkeleton animation(String file)
    {
        return AnimatedSkeleton.fromCollada(ColladaNode.fromFile(storage.open(file)), refreshStatus);
    }

    @Override
    public AnimatedDrawing animatedModel(String file)
    {
        var textureStorage = this.storage.relative(new File(file).getParent());
        var modelSource = new ColladaSkinnedModel(ColladaNode.fromFile(storage.open(file))).load(gl, textureStorage);
        var shaderPlayer = skinShader.player();
        var uniforms = shaderPlayer.uniforms();
        var lightUniform = uniforms.uniformVector("worldLight");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = uniforms.uniformMatrix("projection");
        var shadedModel = modelSource.shade(shaderPlayer);
        return (projection, light, transform, animation) ->
        {
            var drawing = shadedModel.instance(animation);
            Drawing initializedDrawing = () ->
            {
                lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
                drawing.draw();
            };
            return shaderPlayer.play(gl.clockwise(gl.depthOn(gl.smooth(initializedDrawing))));
        };
    }
}