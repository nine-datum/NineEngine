package nine.main;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import nine.drawing.TransformedDrawing;
import nine.function.ErrorPrinter;
import nine.function.FunctionSingle;
import nine.function.UpdateRefreshStatus;
import nine.geometry.SkinnedModel;
import nine.geometry.collada.AnimationFromColladaNode;
import nine.geometry.collada.ColladaSkinnedModel;
import nine.geometry.collada.FileColladaNode;
import nine.geometry.collada.Skeleton;
import nine.input.Keyboard;
import nine.input.Mouse;
import nine.input.WASD_Vector2f;
import nine.io.FileStorage;
import nine.io.Storage;
import nine.lwjgl.LWJGL_Keyboard;
import nine.lwjgl.LWJGL_Mouse;
import nine.lwjgl.LWJGL_OpenGL;
import nine.math.CameraClampVector3fFunction;
import nine.math.Delta;
import nine.math.LocalTime;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fMulChain;
import nine.math.Matrix4fPerspective;
import nine.math.Matrix4fRefreshable;
import nine.math.Matrix4fRotationX;
import nine.math.Matrix4fScale;
import nine.math.Matrix4fTranslation;
import nine.math.OrbitalCameraMatrix4f;
import nine.math.ValueFloatDegreesToRadians;
import nine.math.ValueFloat;
import nine.math.ValueFloatStruct;
import nine.math.Vector2f;
import nine.math.Vector2fAccumulated;
import nine.math.Vector2fFunction;
import nine.math.Vector2fRefreshable;
import nine.math.Vector3f;
import nine.math.Vector3fAccumulated;
import nine.math.Vector3fStruct;
import nine.opengl.CompositeUniform;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;
import nine.opengl.shader.FileShaderSource;
import nine.opengl.shader.ShaderVersionMacro;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Program {

	// The window handle
	private long window;
	int width = 1024;
	int height = 800;

	public void run(String[] args) {

		init();
		loop(args);

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(width, height, "LWJGL", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		glfwSetFramebufferSizeCallback(window, (d, w, h) ->
		{
			width = w;
			height = h;
			GL11.glViewport(0, 0, w, h);
		});

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop(String[] args) {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.5f, 0.5f, 0.7f, 0f);
		
		UpdateRefreshStatus updateStatus = new UpdateRefreshStatus();		

		Storage storage = new FileStorage();

		OpenGL gl = new LWJGL_OpenGL();
		Shader shader = gl.compiler().createProgram(
			new FileShaderSource(storage.open("shaders/diffuse_skin_vertex.glsl"), new ShaderVersionMacro("400")),
			new FileShaderSource(storage.open("shaders/diffuse_fragment.glsl"), new ShaderVersionMacro("400")), acceptor ->
		{
			acceptor.call(0, "position");
			acceptor.call(1, "texcoord");
			acceptor.call(2, "normal");
		});

		ValueFloat time = new LocalTime();
		ValueFloat timeDelta = new Delta(time, updateStatus);
		FPSCounter fps = new FPSCounter(time, System.out::println);
		Mouse mouse = new LWJGL_Mouse(window, updateStatus);
		Keyboard keyboard = new LWJGL_Keyboard(window, updateStatus);

		Vector2f playerMovement = new Vector2fRefreshable(
			new WASD_Vector2f(keyboard).normalized(),
			updateStatus);
			
		Vector2f playerPosition = new Vector2fAccumulated(
			playerMovement.mul(timeDelta.mul(ValueFloat.newFloat(3f))),
			Vector2fFunction.identity, updateStatus);
		
		Matrix4f camera = new OrbitalCameraMatrix4f(
			Vector3f.newXYZ(0f, 2f, 0f).add(Vector3f.newXZ(playerPosition)),
			new Vector3fAccumulated(
				Vector3f.newYX(mouse.delta()).mul(
				timeDelta.mul(ValueFloat.newFloat(0.1f))),
				new CameraClampVector3fFunction(), updateStatus),
			ValueFloat.newFloat(5f));

		Matrix4f projection = new Matrix4fRefreshable(new Matrix4fMul(new Matrix4fPerspective(
			a -> a.call(width / (float)height),
			ValueFloat.newFloat(60f).degreesToRadians(),
			ValueFloat.newFloat(0.1f),
			ValueFloat.newFloat(100f)),
			camera), updateStatus);

		ShaderPlayer shaderPlayer = shader.player().uniforms(u ->
			new CompositeUniform(
				u.uniformVector("worldLight", new Vector3fStruct(0f, 0f, 1f)),
				u.uniformMatrix("projection", projection)));
		
		Vector3fStruct position = new Vector3fStruct();

		Matrix4f world = new Matrix4fMulChain(
			new Matrix4fTranslation(position),
			new Matrix4fRotationX(new ValueFloatDegreesToRadians(-90f)),
			new Matrix4fScale(new Vector3fStruct(1f, 1f, 1f)));

		Skeleton idle = new AnimationFromColladaNode(new FileColladaNode(storage.open("models/Human_Anim_Idle_Test.dae"), ErrorPrinter.instance), updateStatus);
		Skeleton walk = new AnimationFromColladaNode(new FileColladaNode(storage.open("models/Human_Anim_Walk_Test.dae"), ErrorPrinter.instance), updateStatus);

		SkinnedModel model = new ColladaSkinnedModel(new FileColladaNode(storage.open(args[0]), ErrorPrinter.instance))
			.load(gl, storage);

		FunctionSingle<Skeleton, Drawing> animatedDrawing = a -> model.load((key, bone) -> a.transform(key))
			.instance(shaderPlayer, updateStatus);

		FunctionSingle<Drawing, Drawing> finalDrawing = d -> gl.clockwise(gl.depthOn(gl.smooth(d)));

		Drawing player = new PlayerDrawing(
			playerMovement,
			playerPosition,
			finalDrawing.call(animatedDrawing.call(idle)),
			finalDrawing.call(animatedDrawing.call(walk)),
			(transform, drawing) -> new TransformedDrawing(
				new Matrix4fMul(transform, new Matrix4fRotationX(new ValueFloatDegreesToRadians(-90f))),
				shader.player(), drawing));


		Drawing idleDrawing = new TransformedDrawing(world, shader.player(), finalDrawing.call(animatedDrawing.call(idle)));

		int instancesNumber = Integer.valueOf(args[1]);
		int instancesRow = Integer.valueOf(args[2]);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			updateStatus.update();
			fps.frame();

			player.draw();
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

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{
		if(args.length == 0) args = new String[] { "models/Human_Anim_Idle_Test.dae", "100", "10" };
		new Program().run(args);
	}
}