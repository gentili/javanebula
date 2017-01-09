package ca.mcpnet.javanebula;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;

import ca.mcpnet.javanebula.GL.Axis;
import ca.mcpnet.javanebula.GL.Camera;
import ca.mcpnet.javanebula.GL.Nebula;
import ca.mcpnet.javanebula.GL.PixellationFBO;
import ca.mcpnet.javanebula.GL.Projection;
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
	private Axis _axis;
	
	public TestNebula() throws LWJGLException, IOException {
		// Display Init
		Display.setFullscreen(true);
		Display.setDisplayMode(Display.getDesktopDisplayMode());
		// Display.setDisplayMode(new DisplayMode(1920,1200));
		_log.info(Display.getDesktopDisplayMode());
		Display.setTitle("Nebula Screensaver");
		Display.setVSyncEnabled(true);
		
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttribs = new ContextAttribs(3,2);
		contextAttribs = contextAttribs.withForwardCompatible(true);
		contextAttribs = contextAttribs.withProfileCore(true);
		Display.create(pixelFormat, contextAttribs);
		
		_log.info("LWJGL version:  " + org.lwjgl.Sys.getVersion());
		_log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		_shaderProgramManager = new ShaderProgramManager();

		_projection = new Projection();
		_camera = new Camera();
		
		_nebula = new Nebula();
		_nebula.setInterpolations(1f, 1f);
		_pixellationFBO = new PixellationFBO(3);
		_axis = new Axis();
		_axis.setTranslation(0, 0, 0);
	}
	
	public void run() {
		Util.checkGLError();

		// Set up the projection matrix
		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		_projection.fromPerspective(60.0f, aspect, 0.1f, 10.0f);
		_shaderProgramManager.setShaderProgram3DMatrixes(_projection.getProjectionMatrixFloatBuffer(), null);
		
		_camera.setRadius(4.5f);
		_camera.setUpVector(Vector3f.UNIT_Y);
		_camera.setTarget(Vector3f.ZERO);
		_camera.lookAtTarget();
		
		int vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
        while(!Display.isCloseRequested()) {
    		Util.checkGLError();
        	// Do the graphics stuff
    		float dx = Mouse.getDX();
    		float dy = Mouse.getDY();
    		float dr = Mouse.getDWheel();
    		
    		boolean rightButtonDown = Mouse.isButtonDown(1);
    		
    		if (rightButtonDown) {
    			_camera.addHorizontalRotationAboutTarget(-dx/400f);
    			_camera.addVerticalRotationAboutTarget(-dy/400f);
    		} else {
    			_camera.addHorizontalRotationAboutTarget(0.001f);
    		}
    		if (dr != 0) {
        		_camera.addRadius(dr/10000f);
    			_log.info("camera radius set to "+_camera.getRadius());
    		}
    		_camera.lookAtTarget();

    		_shaderProgramManager.setShaderProgram3DMatrixes(null, _camera.getViewMatrixFloatBuffer());
    		Util.checkGLError();
    		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    		Util.checkGLError();
        	GL11.glScissor(0, 0, Display.getWidth(), Display.getHeight());
    		Util.checkGLError();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    		Util.checkGLError();

            _pixellationFBO.begin();
    		_nebula.draw();
    		_pixellationFBO.end();
            // _axis.draw();
    		
    		Display.update();
        }
        Display.destroy();		
	}

}
