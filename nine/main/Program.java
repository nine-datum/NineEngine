package nine.main;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import nine.geometry.collada.ColladaModel;
import nine.io.FileStorage;
import nine.io.Storage;
import nine.lwjgl.LWJGL_OpenGL;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fMulChain;
import nine.math.Matrix4fPerspective;
import nine.math.Matrix4fRotationX;
import nine.math.Matrix4fRotationY;
import nine.math.Matrix4fScale;
import nine.math.Matrix4fTransform;
import nine.math.Matrix4fTranslation;
import nine.math.ValueFloatDegreesToRadians;
import nine.math.Time;
import nine.math.ValueFloatStruct;
import nine.math.Vector3fNormalized;
import nine.math.Vector3fStruct;
import nine.opengl.CompositeUniform;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;
import nine.opengl.shader.FileShaderSource;
import nine.opengl.shader.ShaderVersionMacro;

import java.io.File;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Program {

	// The window handle
	private long window;

	public void run() {

		init();
		loop();

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
		window = glfwCreateWindow(500, 500, "LWJGL", NULL, NULL);
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

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.5f, 0.5f, 0.7f, 0f);
		
		

		Storage storage = new FileStorage();

		OpenGL gl = new LWJGL_OpenGL();
		Shader shader = gl.compiler().createProgram(
			new FileShaderSource(storage.open("shaders/diffuse_vertex.glsl"), new ShaderVersionMacro("400")),
			new FileShaderSource(storage.open("shaders/diffuse_fragment.glsl"), new ShaderVersionMacro("400")), acceptor ->
		{
			acceptor.call(0, "position");
			acceptor.call(1, "texcoord");
			acceptor.call(2, "normal");
		});

		Matrix4f cameraInversed = new Matrix4fTranslation(new Vector3fStruct(0f, -1f, 2.5f));

		Matrix4f projection = new Matrix4fMul(new Matrix4fPerspective(
			new ValueFloatStruct(1f),
			new ValueFloatDegreesToRadians(60f),
			new ValueFloatStruct(0.1f),
			new ValueFloatStruct(100f)),
			cameraInversed);

		ShaderPlayer shaderPlayer = shader.player().uniforms(u ->
			new CompositeUniform(
				u.uniformVector("worldLight", new Vector3fNormalized(new Vector3fStruct(0f, 0f, 1f))),
				u.uniformMatrix("projection", projection)));
		
		Matrix4f world = new Matrix4fMulChain(
			new Matrix4fRotationY(new Time()),
			new Matrix4fRotationX(new ValueFloatDegreesToRadians(0f)),
			new Matrix4fTranslation(new Vector3fStruct(0f, 0f, 0f)));

		Drawing cube = new ColladaModel(new File("resources/models/Knight.dae"), System.out::println).load(gl);

		BodyPart body = new BodyPart(new Matrix4fTransform(
			new Vector3fStruct(0f, 0f, 0f),
			new Vector3fStruct(0f, 0f, 0f)
		),
		new Matrix4fScale(new Vector3fStruct(0.01f, 0.01f, 0.01f)),
		cube);

		Drawing drawing = gl.clockwise(gl.depthOn(gl.smooth(body.drawing(shaderPlayer, world))));
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			drawing.draw();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{
		new Program().run();
	}
}