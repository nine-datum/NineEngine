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
import nine.input.WASD_Vector2f;
import nine.io.FileStorage;
import nine.io.Storage;
import nine.lwjgl.LWJGL_Window;
import nine.math.CameraClampVector3fFunction;
import nine.math.Delta;
import nine.math.LocalTime;
import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.math.ValueFloat;
import nine.math.Vector2f;
import nine.math.Vector2fIntegral;
import nine.math.Vector2fFunction;
import nine.math.Vector2fRefreshable;
import nine.math.Vector3f;
import nine.math.Vector3fIntegral;
import nine.math.Vector3fStruct;
import nine.opengl.CompositeUniform;
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
		if(cli_args.length == 0) args = new String[] { "models/Human_Anim_Idle_Test.dae", "100", "10" };
		else args = cli_args;

		new LWJGL_Window().run(windowStartWidth, windowStartHeight, window ->
		{
			UpdateRefreshStatus updateStatus = new UpdateRefreshStatus();		

			Storage storage = new FileStorage();

			OpenGL gl = (OpenGL)storage.loadScript("scripts/opengl.jena").toObject(OpenGL.class);
			Mouse mouse = (Mouse)storage.loadScript("scripts/mouse.jena").managedCall(window, updateStatus).toObject(Mouse.class);
			Keyboard keyboard = (Keyboard)storage.loadScript("scripts/keyboard.jena").managedCall(window, updateStatus).toObject(Keyboard.class);

			ShaderLoader shaderLoader = Shader.loader(storage, gl);
			Shader skinShader = shaderLoader.load("shaders/diffuse_skin_vertex.glsl","shaders/diffuse_fragment.glsl");
			Shader diffuseShader = shaderLoader.load("shaders/diffuse_vertex.glsl", "shaders/diffuse_fragment.glsl");

			ValueFloat time = new LocalTime();
			ValueFloat timeDelta = new Delta(time, updateStatus);
			FPSCounter fps = new FPSCounter(time, System.out::println);

			Vector3f cameraRotation = new Vector3fIntegral(
				Vector3f.newYX(mouse.delta()).mul(
				timeDelta.mul(ValueFloat.of(0.1f))),
				new CameraClampVector3fFunction(), updateStatus).negative();

			Vector2f playerMovement = new Vector2fRefreshable(
				new WASD_Vector2f(keyboard).cached(updateStatus).normalized().rotate(cameraRotation.y()),
				updateStatus);
				
			Vector2f playerPosition = new Vector2fIntegral(
				playerMovement.mul(timeDelta.mul(ValueFloat.of(3f))),
				Vector2fFunction.identity, updateStatus);

			Matrix4f camera = Matrix4f.orbitalCamera(
				Vector3f.newXYZ(0f, 2f, 0f).add(Vector3f.newXZ(playerPosition)),
				cameraRotation,
				ValueFloat.of(5f));

			int[] windowSize = {windowStartWidth, windowStartHeight};

			Matrix4f projection = Matrix4f.perspective(
				a -> a.call(windowSize[0] / (float)windowSize[1]),
				ValueFloat.of(60f).degreesToRadians(),
				ValueFloat.of(0.1f),
				ValueFloat.of(100f)).mul(
				camera).cached(updateStatus);

			Vector3f worldLight = Vector3f.newXYZ(0f, -1f, 1f).normalized();
			
			Vector3fStruct position = new Vector3fStruct();
			Matrix4f humanWorld = Matrix4f.translation(position).mul(
				Matrix4f.rotationX(ValueFloat.of(-90f).degreesToRadians()));

			AnimatedSkeleton idle = AnimatedSkeleton.fromCollada(new FileColladaNode(storage.open("models/Human_Anim_Idle_Test.dae"), ErrorPrinter.instance), updateStatus);
			AnimatedSkeleton walk = AnimatedSkeleton.fromCollada(new FileColladaNode(storage.open("models/Human_Anim_Walk_Test.dae"), ErrorPrinter.instance), updateStatus);

			Graphics graphics = Graphics.collada(gl, diffuseShader, skinShader, projection, worldLight, storage, updateStatus);

			var human = graphics.animatedModel(args[0]);

			FunctionSingle<Drawing, Drawing> finalDrawing = d -> gl.clockwise(gl.depthOn(gl.smooth(d)));

			var groundTexture = gl.texture(storage.open("textures/ground.jpg"));
			var groundDrawing = Geometry.lineString(
				gl,
				Vector2f.newXY(10f, 10f),
				Buffer.of(
					MaterialPoint.of(ValueFloat.of(200f), Vector3f.newXYZ(0f, 0f, -100f), Vector3f.newY(1f)),
					MaterialPoint.of(ValueFloat.of(200f), Vector3f.newXYZ(0f, 0f, 100f), Vector3f.newY(1f))
			));

			var caveDrawing = Geometry.brush(gl)
				.plane(Vector3f.newXYZ(0f, 4f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 2f), Vector2f.newXY(4f, 4f))
				.plane(Vector3f.newXYZ(-2f, 2f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 0.5f), Vector2f.newXY(4f, 4f))
				.plane(Vector3f.newXYZ(2f, 2f, 0f), Vector3f.newXYZ(0f, 0f, (float)Math.PI * 1.5f), Vector2f.newXY(4f, 4f))
				.drawing();


			ShaderPlayer diffuseShaderPlayer = diffuseShader.player().uniforms(u ->
			new CompositeUniform(
				u.uniformVector("worldLight", worldLight),
				u.uniformMatrix("projection", projection)));

			var levelDrawing = finalDrawing.call(groundTexture.apply(Drawing.of(groundDrawing, caveDrawing)))
				.transform(Matrix4fIdentity.identity, diffuseShaderPlayer);

			Drawing player = new PlayerDrawing(
				playerMovement,
				playerPosition,
				idle.animate(time),
				walk.animate(time),
				(transform, skeleton) -> human.animate(
					transform.mul(Matrix4f.rotationX(ValueFloat.of(-90f).degreesToRadians())),
					skeleton));


			Drawing idleDrawing = human.animate(humanWorld, idle.animate(time));

			int instancesNumber = Integer.valueOf(args[1]);
			int instancesRow = Integer.valueOf(args[2]);

			return (width, height) ->
			{
				windowSize[0] = width;
				windowSize[1] = height;

				updateStatus.update();
				fps.frame();

				player.draw();
				levelDrawing.draw();
				int l = instancesNumber;
				int r = instancesRow;
				for(int i = 0; i < l; i++)
				{
					int px = (i % r) * 2 - 2 * (r / 2);
					int py = ((i / r) % r) * 2 - 2 * (r / 2);
					position.x = px;
					position.z = py;
					if(px != 0 || py != 0) idleDrawing.draw();
				}
			};
		});
	}
}