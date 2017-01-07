package ca.mcpnet.javanebula;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import ca.mcpnet.javanebula.GL.Camera;
import ca.mcpnet.javanebula.GL.Nebula;
import ca.mcpnet.javanebula.GL.PixellationFBO;
import ca.mcpnet.javanebula.GL.Projection;
import ca.mcpnet.javanebula.jme.BufferUtils;
import ca.mcpnet.javanebula.jme.FastMath;
import ca.mcpnet.javanebula.jme.Vector3f;
		
public class TestNebula {

	Logger _log = Logger.getLogger("TestGui");
	
	public static void main(String[] args) {
		Properties logprops = new Properties();
		logprops.setProperty("log4j.rootLogger", "DEBUG, A1");
		logprops.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		logprops.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		logprops.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%-20t] %-5p %c.%M %x - %m%n");
		PropertyConfigurator.configure(logprops);
		
		Logger log = Logger.getLogger("main()");
		log.info("Starting GameClient");

		try {
			new TestNebula().run();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		log.info("Main Thread Exiting");
	}    

	private Projection _projection;
	private Camera _camera;

	private ShaderProgramManager _shaderProgramManager;
	
	private Nebula _nebula;
	private PixellationFBO _pixellationFBO;
	
	private ByteBuffer _framebuffer;
	private int[] _framearray;
	private int _curframe;

	public TestNebula() throws LWJGLException, IOException {
		// Display Init
		Display.setDisplayMode(new DisplayMode(640,480));
		Display.setTitle("Nebula Test");
		Display.setVSyncEnabled(true);
		
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttribs = new ContextAttribs(3,2);
		contextAttribs = contextAttribs.withProfileCompatibility(true);
		// contextAttribs = contextAttribs.withForwardCompatible(true);
		// contextAttribs = contextAttribs.withProfileCore(true);
		Display.create(pixelFormat, contextAttribs);
		
		_log.info("LWJGL version:  " + org.lwjgl.Sys.getVersion());
		_log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		_shaderProgramManager = new ShaderProgramManager();

		_projection = new Projection();
		_camera = new Camera();
		
		_nebula = new Nebula();
		_nebula.setInterpolations(1, 1);
		_pixellationFBO = new PixellationFBO(6);
		
		_framebuffer = BufferUtils.createByteBuffer(Display.getWidth()*Display.getHeight()*4);
		_framearray = new int[Display.getWidth()*Display.getHeight()];
		
	}
	
	public void run() {

		// Set up the projection matrix
		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		_projection.fromPerspective(60.0f, aspect, 0.1f, 10.0f);
		_shaderProgramManager.setShaderProgram3DMatrixes(_projection.getProjectionMatrixFloatBuffer(), null);
		
		_camera.setRadius(4.5f);
		_camera.setUpVector(Vector3f.UNIT_Y);
		_camera.setTarget(Vector3f.ZERO);
		_camera.lookAtTarget();
		
        while(!Display.isCloseRequested()) {
    		double time = System.currentTimeMillis();
    		time = System.nanoTime()/1000000;
    		float a = (float) (time/2000 % (FastMath.TWO_PI));
        	// Do the graphics stuff
    		Vector3f mouseVector = new Vector3f(Mouse.getX(),Mouse.getY(),0);
    		float dx = Mouse.getDX();
    		float dy = Mouse.getDY();
    		float dr = Mouse.getDWheel();
    		
    		boolean leftButtonDown = Mouse.isButtonDown(0);
    		boolean rightButtonDown = Mouse.isButtonDown(1);
    		boolean middleButtonDown = Mouse.isButtonDown(2);
    		
    		_curframe++;
    		
    		if (rightButtonDown) {
    			_camera.addHorizontalRotationAboutTarget(-dx/400f);
    			_camera.addVerticalRotationAboutTarget(-dy/400f);
    		} else {
    			_camera.addHorizontalRotationAboutTarget(0.01f);
    		}
    		if (dr != 0) {
        		_camera.addRadius(dr/10000f);
    			_log.info("camera radius set to "+_camera.getRadius());
    		}
    		_camera.lookAtTarget();

    		_shaderProgramManager.setShaderProgram3DMatrixes(null, _camera.getViewMatrixFloatBuffer());

    		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        	GL11.glScissor(0, 0, Display.getWidth(), Display.getHeight());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

            _pixellationFBO.begin();
    		_nebula.draw();
    		_pixellationFBO.end();
    		
    		Display.update();
        }
        Display.destroy();		
	}

}
