package nine.geometry.assimp;

import nine.geometry.AnimatedSkeleton;
import nine.geometry.Model;
import nine.main.TransformedDrawing;
import nine.function.RefreshStatus;
import nine.game.AnimatedDrawing;
import nine.game.Graphics;

import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;

import java.util.stream.IntStream;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D.Buffer;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

public class AssimpGraphics implements Graphics
{
	OpenGL gl;
	Shader skinShader;
	Shader diffuseShader;
	RefreshStatus refreshStatus;
	
	public AssimpGraphics(
		OpenGL gl,
		Shader skinShader,
		Shader diffuseShader,
		RefreshStatus refreshStatus)
	{
		this.gl = gl;
		this.skinShader = skinShader;
		this.diffuseShader = diffuseShader;
		this.refreshStatus = refreshStatus;
	}
	
	static Float[] vertexDataFromBuffer(int size, int dim, Buffer buffer)
	{
		Float[] fs = new Float[size * dim];
		for(int i = 0; i < size; i++)
		{
			var v = buffer.get(i);
			int c = i * dim;
			float[] vs = { v.x(), v.y(), v.z() };
			for(int d = 0; d < dim; d++) fs[c + d] = vs[d];
		}
		return fs;
	}

	@Override
	public AnimatedSkeleton animation(String file, String boneType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformedDrawing model(String file) {
		AIScene scene = Assimp.aiImportFile(file,
			Assimp.aiProcess_Triangulate |	
			Assimp.aiProcess_LimitBoneWeights);
		var meshes = scene.mMeshes();
		var drawings = IntStream.range(0, scene.mNumMeshes()).boxed().map(meshIndex ->
		{
			try (var mesh = AIMesh.create(meshes.get(meshIndex)))
			{
				int verts = mesh.mNumVertices();
				
				var vertices = vertexDataFromBuffer(verts, 3, mesh.mVertices());
				var normals = vertexDataFromBuffer(verts, 3, mesh.mNormals());
				var uvs = vertexDataFromBuffer(verts, 2, mesh.mTextureCoords(0));
				
				Integer[] indices = new Integer[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
				int i = 0;
				
				for(int f = 0; f < mesh.mNumFaces(); f++)
				{
					AIFace face = mesh.mFaces().get(f);
					for(int ind = 0; ind < face.mNumIndices(); ind++)
					{
						indices[i++] = (face.mIndices().get(ind));
					}
				}
				
				return gl.vao(nine.buffer.Buffer.of(indices))
					.attribute(3, nine.buffer.Buffer.of(vertices).fromRightToLeftHanded())
					.attribute(2, nine.buffer.Buffer.of(uvs))
					.attribute(3, nine.buffer.Buffer.of(normals).fromRightToLeftHanded()).drawing();
			}
		}).toArray(Drawing[]::new);
		var compositeDrawing = Drawing.of(drawings);
		var diffuseShader = this.diffuseShader.player();
		
		var uniforms = diffuseShader.uniforms();
		var lightUniform = uniforms.uniformVector("worldLight");
        var transformUniform = uniforms.uniformMatrix("transform");
        var projectionUniform = uniforms.uniformMatrix("projection");
		
        return (projection, light, transform) ->
        {
            Drawing initializedDrawing = () ->
            {
                lightUniform.load(light);
                transformUniform.load(transform);
                projectionUniform.load(projection);
                compositeDrawing.draw();
            };
            return diffuseShader.play(gl.depthOn(gl.smooth(initializedDrawing)));
        };
	}

	@Override
	public AnimatedDrawing animatedModel(String file) {
		// TODO Auto-generated method stub
		return null;
	}
}
