package nine.main;

import nine.function.RefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.geometry.collada.ColladaNode;
import nine.geometry.collada.ColladaSkinnedModel;
import nine.io.Storage;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.Uniform;

public class ColladaOpenGLGrahics implements Graphics
{
    OpenGL gl;
    Shader diffuseShader;
    Shader skinShader;
    Matrix4f projection;
    Vector3f lightDirection;
    Storage storage;
    RefreshStatus refreshStatus;

    public ColladaOpenGLGrahics(
        OpenGL gl,
        Shader diffuseShader,
        Shader skinShader,
        Matrix4f projection,
        Vector3f lightDirection,
        Storage storage,
        RefreshStatus refreshStatus)
    {
        this.gl = gl;
        this.diffuseShader = diffuseShader;
        this.skinShader = skinShader;
        this.projection = projection;
        this.lightDirection = lightDirection;
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
        var modelSource = new ColladaSkinnedModel(ColladaNode.fromFile(storage.open(file))).load(gl, storage);
        return (transform, animation) ->
        {
            var shaderPlayer = skinShader.player().uniforms(uniforms ->
            {
                return Uniform.of(
                    uniforms.uniformVector("worldLight", lightDirection),
                    uniforms.uniformMatrix("transform", transform),
                    uniforms.uniformMatrix("projection", projection));
            });
            return modelSource.load(animation).instance(shaderPlayer);
        };
    }
}