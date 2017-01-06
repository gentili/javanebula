package ca.mcpnet.demurrage.GameClient;

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

import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.Nebula;
import ca.mcpnet.demurrage.GameClient.GL.PixellationFBO;
import ca.mcpnet.demurrage.GameClient.GL.Projection;
import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
		
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
	private boolean _gifwrite;
	private AnimatedGifEncoder _gifEncoder;
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
    		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
    			if (!_gifwrite) {
    				_curframe = 0;
    				_gifwrite = true;
    				_log.debug("Starting gif write");
    				
    				_gifEncoder = new AnimatedGifEncoder();
    				_gifEncoder.setDelay(50);
    				_gifEncoder.setRepeat(0);
    				_gifEncoder.setDispose(0);
    				_gifEncoder.setQuality(1);
    				_gifEncoder.start("testgif.gif");
    			}
    		}
    		if (_curframe >= 100) {
    			if (_gifwrite) {
    				_gifwrite = false;
    				_log.debug("Stopping gif write");
    				_gifEncoder.finish();
    			}
    		}
    		
    		if (rightButtonDown) {
    			_camera.addHorizontalRotationAboutTarget(-dx/400f);
    			_camera.addVerticalRotationAboutTarget(-dy/400f);
    		}
    		if (dr != 0) {
        		_camera.addRadius(dr/10000f);
    			_log.info("camera radius set to "+_camera.getRadius());
    		}
    		if (_gifwrite) {
    			_camera.addHorizontalRotationAboutTarget((float) (2*Math.PI/100));
    		}
    		_camera.lookAtTarget();

    		_shaderProgramManager.setShaderProgram3DMatrixes(null, _camera.getViewMatrixFloatBuffer());

    		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        	GL11.glScissor(0, 0, Display.getWidth(), Display.getHeight());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

            _pixellationFBO.begin();
    		_nebula.draw();
    		_pixellationFBO.end();

    		// Take a screenshot
    		if (_gifwrite) {
    			int width = Display.getWidth();
    			int height = Display.getHeight();
    			GL11.glReadBuffer(GL11.GL_FRONT);
	    		_framebuffer.rewind();
	    		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _framebuffer);
	    		_framebuffer.rewind();
	    		for (int x = 0; x < width; x++) {
	    			for (int y = 0; y < height; y++) {
	    				int i = (x + (width * y)) * 4;
		    			int r = _framebuffer.get(i) & 0xFF;
		    			int g = _framebuffer.get(i+1) & 0xFF;
		    			int b = _framebuffer.get(i+2) & 0xFF;
		    			_framearray[x + width * y] = (0xFF << 24) | (r << 16) | (g << 8) | b; 
	    			}
	    		}
	    		BufferedImage bi = new BufferedImage(Display.getWidth(),Display.getHeight(),BufferedImage.TYPE_INT_RGB);
	    		bi.setRGB(0 , 0, Display.getWidth(), Display.getHeight(), _framearray, 0, Display.getWidth());
	    		
	    		_gifEncoder.addFrame(bi);
    		}
    		
    		Display.update();
        }
        Display.destroy();		
	}

}
