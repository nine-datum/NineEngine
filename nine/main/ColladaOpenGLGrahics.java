package nine.main;

import java.io.File;

import nine.function.RefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.geometry.collada.ColladaNode;
import nine.geometry.collada.ColladaSkinnedModel;
import nine.io.Storage;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.Uniform;

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
        return (projection, light, transform, animation) ->
        {
            var shaderPlayer = skinShader.player().uniforms(uniforms ->
            {
                return Uniform.of(
                    uniforms.uniformVector("worldLight", light),
                    uniforms.uniformMatrix("transform", transform),
                    uniforms.uniformMatrix("projection", projection));
            });
            var drawing = modelSource.load(animation).instance(shaderPlayer);
            return gl.clockwise(gl.depthOn(gl.smooth(drawing)));
        };
    }
}