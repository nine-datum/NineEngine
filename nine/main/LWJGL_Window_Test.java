package nine.main;

import nine.buffer.Buffer;
import nine.function.ErrorPrinter;
import nine.function.FunctionSingle;
import nine.function.UpdateRefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.geometry.collada.FileColladaNode;
import nine.geometry.procedural.Geometry;
import nine.geometry.procedural.MaterialPoint;
import nine.input.Keyboard;
import nine.input.Mouse;
import nine.io.FileStorage;
import nine.io.Storage;
import nine.lwjgl.LWJGL_Window;
import nine.math.Delta;
import nine.math.LocalTime;
import nine.math.Matrix4f;
import nine.math.FloatFunc;
import nine.math.Vector2f;
import nine.math.Vector3f;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.ShaderLoader;
import nine.opengl.ShaderPlayer;

public class LWJGL_Window_Test
{
	public static void main(String[] cli_args)
	{
		int windowStartWidth = 1200;
		int windowStartHeight = 800;

		String[] args;
		if(cli_args.length == 0) args = new String[] { "resources/models/Knight/Idle.dae", "100", "10" };
		else args = cli_args;

		new LWJGL_Window().run(windowStartWidth, windowStartHeight, window ->
		{
			UpdateRefreshStatus updateStatus = new UpdateRefreshStatus();		

			Storage storage = new FileStorage();

			OpenGL gl = (OpenGL)storage.loadScript("resources/scripts/opengl.jena").toObject(OpenGL.class);
			Mouse mouse = (Mouse)storage.loadScript("resources/scripts/mouse.jena").managedCall(window, updateStatus).toObject(Mouse.class);
			Keyboard keyboard = (Keyboard)storage.loadScript("resources/scripts/keyboard.jena").managedCall(window, updateStatus).toObject(Keyboard.class);

			ShaderLoader shaderLoader = Shader.loader(storage, gl);
			Shader skinShader = shaderLoader.load("resources/shaders/diffuse_skin_vertex.glsl","resources/shaders/diffuse_fragment.glsl");
			Shader diffuseShader = shaderLoader.load("resources/shaders/diffuse_vertex.glsl", "resources/shaders/diffuse_fragment.glsl");

			AnimatedSkeleton idle = AnimatedSkeleton.fromCollada(new FileColladaNode(storage.open("resources/models/Knight/LongSword_Idle.dae"), ErrorPrinter.instance), updateStatus);
			AnimatedSkeleton walk = AnimatedSkeleton.fromCollada(new FileColladaNode(storage.open("resources/models/Knight/Walk.dae"), ErrorPrinter.instance), updateStatus);

			Graphics graphics = Graphics.collada(gl, diffuseShader, skinShader, storage, updateStatus);

			var human = graphics.model(args[0]);

			FunctionSingle<Drawing, Drawing> finalDrawing = d -> gl.clockwise(gl.depthOn(gl.smooth(d)));

			var groundTexture = gl.texture(storage.open("resources/textures/ground.jpg"));
			var groundDrawing = Geometry.lineString(
				gl,
				Vector2f.newXY(10f, 10f),
				Buffer.of(
					MaterialPoint.of(200f, Vector3f.newXYZ(0f, 0f, -100f), Vector3f.newY(1f)),
					MaterialPoint.of(200f, Vector3f.newXYZ(0f, 0f, 100f), Vector3f.newY(1f))
			));

			var caveDrawing = Geometry.brush(gl)
				.plane(Vector3f.newXYZ(0f + 1, 4f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 2f), Vector2f.newXY(4f, 4f))
				.plane(Vector3f.newXYZ(-2f + 1, 2f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 0.5f), Vector2f.newXY(4f, 4f))
				.plane(Vector3f.newXYZ(2f + 1, 2f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 1.5f), Vector2f.newXY(4f, 4f))
				.drawing();

			ShaderPlayer diffuseShaderPlayer = diffuseShader.player();
			var diffuseShaderUniforms = diffuseShaderPlayer.uniforms();

			var worldLightUniform = diffuseShaderUniforms.uniformVector("worldLight");
			var projectionUniform = diffuseShaderUniforms.uniformMatrix("projection");
			var transformUniform = diffuseShaderUniforms.uniformMatrix("transform");


			FloatFunc time = new LocalTime();
			FloatFunc timeDelta = new Delta(time, updateStatus);
			FPSCounter fps = new FPSCounter(time, System.out::println);

			int instancesNumber = Integer.valueOf(args[1]);
			int instancesRow = Integer.valueOf(args[2]);
			Vector2f[] mouseInput = { Vector2f.newXY(0f, 0f) };
			Vector3f[] wasdeq = { Vector3f.newXYZ(0f, 0f, 0f) };

			return (width, height) ->
			{
				mouseInput[0] = mouseInput[0].add(mouse.delta().mul(timeDelta.value() * 0.1f));
				wasdeq[0] = wasdeq[0].add(Vector3f.wasdeq(keyboard).mul(timeDelta.value() * 3f));
				
				Vector3f cameraRotation = Vector3f.newXY(-mouseInput[0].y, mouseInput[0].x);
				cameraRotation = cameraRotation.add(Vector3f.newZ(wasdeq[0].z));

				Matrix4f camera = Matrix4f.orbitalCamera(
					Vector3f.newXYZ(wasdeq[0].x, 2f, wasdeq[0].y),
					cameraRotation,
					5f);

				Vector3f worldLight = Vector3f.newXYZ(0f, -1f, 1f).normalized();

				Matrix4f projection = Matrix4f.perspective(
					width / (float)height,
					FloatFunc.toRadians(60f),
					0.1f,
					100f).mul(camera);
			
				Vector3f position = Vector3f.newXYZ(0f, 0f, 0f);
				Matrix4f humanWorld = Matrix4f.translation(position).mul(
					Matrix4f.rotationX(FloatFunc.toRadians(0f)));

				var levelDrawing = finalDrawing.call(groundTexture.apply(Drawing.of(groundDrawing, caveDrawing)));

				updateStatus.update();
				fps.frame();
				
				diffuseShaderPlayer.play(() ->
				{
					worldLightUniform.load(worldLight);
					projectionUniform.load(projection);
					transformUniform.load(Matrix4f.identity);
					levelDrawing.draw();
				}).draw();
				
				int l = instancesNumber;
				int r = instancesRow;
				for(int i = 0; i < l; i++)
				{
					int px = (i % r) * 2 - 2 * (r / 2);
					int py = ((i / r) % r) * 2 - 2 * (r / 2);

					if(px != 0 || py != 0)
					{
						Drawing idleDrawing = human.transform(
							projection,
							worldLight,
							Matrix4f.translation(Vector3f.newXYZ(px, 0f, py)).mul(humanWorld));

						idleDrawing.draw();
					}
				}
			};
		});
	}
}