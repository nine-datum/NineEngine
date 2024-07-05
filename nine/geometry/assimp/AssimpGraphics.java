package nine.geometry.assimp;

import nine.geometry.AnimatedSkeleton;
import nine.geometry.Model;
import nine.io.Storage;
import nine.main.TransformedDrawing;
import nine.function.RefreshStatus;
import nine.game.AnimatedDrawing;
import nine.game.Graphics;
import nine.drawing.ColorFloatStruct;

import nine.opengl.Drawing;
import nine.opengl.DrawingAttributeBuffer;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;
import nine.opengl.Texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D.Buffer;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

public class AssimpGraphics implements Graphics
{
	OpenGL gl;
	Shader skinShader;
	Shader diffuseShader;
	Storage storage;
	RefreshStatus refreshStatus;
	
	public AssimpGraphics(
		OpenGL gl,
		Shader skinShader,
		Shader diffuseShader,
		Storage storage,
		RefreshStatus refreshStatus)
	{
		this.gl = gl;
		this.skinShader = skinShader;
		this.diffuseShader = diffuseShader;
		this.storage = storage;
		this.refreshStatus = refreshStatus;
	}
	
	static Float[] vertexDataFromBuffer(int size, int dim, Buffer buffer)
	{
		Float[] fs = new Float[size * dim];
		float[] vs = { 0, 0, 0 };
		
		for(int i = 0; i < size; i++)
		{
			int c = i * dim;
			
			if(buffer != null)
			{
				var v = buffer.get(i);
				vs[0] = v.x();
				vs[1] = v.y();
				vs[2] = v.z();
			}
			else
			{
				vs[0] = vs[1] = vs[2] = 0;
			}
			
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
		var diffuseShader = this.diffuseShader.player();
		var uniforms = diffuseShader.uniforms();
		var colorUniform = uniforms.uniformColor("color");
		
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
				
				var drawing = gl.vao(nine.buffer.Buffer.of(indices))
					.attribute(3, nine.buffer.Buffer.of(vertices).fromRightToLeftHanded())
					.attribute(2, nine.buffer.Buffer.of(uvs))
					.attribute(3, nine.buffer.Buffer.of(normals).fromRightToLeftHanded()).drawing();
				
				var material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
				AIString mTexPath = AIString.calloc();
				Assimp.aiGetMaterialTexture(material, 1, 0, mTexPath, (IntBuffer) null, null, null, null, null, null);
				String textureName = mTexPath.dataString();
				
				Texture texture;
				
				if(!textureName.isBlank())
				{
					String textureFileName = Path.of(new File(file).getParent().toString(), textureName).toString();
					texture = gl.texture(storage.open(textureFileName));
				}
				else
				{
					var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
					img.setRGB(0, 0, java.awt.Color.WHITE.getRGB());
					texture = gl.texture(img);
				}
				drawing = texture.apply(drawing);
				
				var color = AIColor4D.create();
				Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, 0, 0, color);
				var colorStruct = color == null ? new ColorFloatStruct(1, 1, 1, 1) : new ColorFloatStruct(color.r(), color.g(), color.b(), color.a());
				
				var sourceDrawing = drawing;
				drawing = () ->
				{
					colorUniform.load(colorStruct);
					sourceDrawing.draw();
				};
				
				
				return drawing;
			}
		}).toArray(Drawing[]::new);
		var compositeDrawing = Drawing.of(drawings);
		
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
