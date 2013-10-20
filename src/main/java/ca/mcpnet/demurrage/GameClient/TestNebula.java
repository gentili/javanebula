package ca.mcpnet.demurrage.GameClient;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.GlowSphere;
import ca.mcpnet.demurrage.GameClient.GL.Nebula;
import ca.mcpnet.demurrage.GameClient.GL.PixellationFBO;
import ca.mcpnet.demurrage.GameClient.GL.Projection;
import ca.mcpnet.demurrage.GameClient.GL.WireSphere;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
		
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

	public TestNebula() throws LWJGLException, IOException {
		// Display Init
		Display.setDisplayMode(new DisplayMode(300,300));
		Display.setTitle("Nebula Test");
		Display.setVSyncEnabled(true);
		
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttribs = new ContextAttribs(3,2);
		contextAttribs = contextAttribs.withProfileCompatibility(true);
		Display.create(pixelFormat, contextAttribs);
		
		_log.info("LWJGL version:  " + org.lwjgl.Sys.getVersion());
		_log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		_shaderProgramManager = new ShaderProgramManager();

		_projection = new Projection();
		_camera = new Camera();
		
		_nebula = new Nebula();
		_pixellationFBO = new PixellationFBO(1);
	}
	
	public void run() {

		// Set up the projection matrix
		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		_projection.fromPerspective(60.0f, aspect, 0.1f, 10.0f);
		_shaderProgramManager.setShaderProgram3DMatrixes(_projection.getProjectionMatrixFloatBuffer(), null);
		
		_camera.setRadius(2.0f);
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
    		
    		_camera.addRadius(dr/10000f);
    		if (rightButtonDown) {
    			_camera.addHorizontalRotationAboutTarget(-dx/400f);
    			_camera.addVerticalRotationAboutTarget(-dy/400f);
    		}
    		if (middleButtonDown) {
    			_camera.addRadius(dy/400f);
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