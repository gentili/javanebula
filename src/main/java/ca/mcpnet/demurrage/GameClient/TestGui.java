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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.GlowSphere;
import ca.mcpnet.demurrage.GameClient.GL.Projection;
import ca.mcpnet.demurrage.GameClient.GL.WireSphere;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
		
public class TestGui {

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
			new TestGui().run();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		log.info("Main Thread Exiting");
	}    

	private Projection _projection;
	private Camera _camera;

	private ShaderProgramManager _shaderProgramManager;
	private Axis _axis;
	private GlowSphere _glowSphere;
	private WireSphere _wireSphere;
	
	private LWJGLRenderer _renderer;
	private RootWidget _rootWidget;
	private GUI _gui;
	private ThemeManager _theme;

	public TestGui() throws LWJGLException, IOException {
		// Display Init
		Display.setDisplayMode(new DisplayMode(1024,768));
		Display.setTitle("Demurrage GameClient");
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
		_axis = new Axis();
		_glowSphere = new GlowSphere();
		_wireSphere = new WireSphere();

        // TWL Menu Init
		_renderer = new LWJGLRenderer();
        _rootWidget = new RootWidget();
        _rootWidget.setTheme("");
        _gui = new GUI(_rootWidget, _renderer);
        _theme = ThemeManager.createThemeManager(
                TestNetworkClient.class.getResource("/theme.xml"), _renderer);
        _gui.applyTheme(_theme);
	}
	
	private static class RootWidget extends Widget {
		
		private Button _addbutton;
		private Button _delbutton;
		private TargetWidget _targetWidget;
		private Button _setReadoutButton;
		private ReadoutWidget _readoutWidget;
		private Button _addReadoutButton;
		
		public RootWidget() {
	        
	        _addbutton = new Button();
	        _addbutton.setText("Snap Box");
	        _addbutton.addCallback(new Runnable() {

				@Override
				public void run() {
					_targetWidget.snapToTarget(500);
				}
	        });
	        add(_addbutton); 

	        _delbutton = new Button();
	        _delbutton.setText("Toggle Pulse");
	        _delbutton.addCallback(new Runnable() {

	        	boolean _pulse = false;
				@Override
				public void run() {
					_pulse = !_pulse;
					_targetWidget.setPulse(_pulse);
				}
	        });
	        add(_delbutton);
	        
	        _targetWidget = new TargetWidget();
	        add(_targetWidget);
	        _targetWidget.setTargetSize(40);
	        _targetWidget.setTargetPosition(10, 10);
	        
	        
	        _setReadoutButton = new Button();
	        _setReadoutButton.setText("Set Readout Text");
	        _setReadoutButton.addCallback(new Runnable() {

				@Override
				public void run() {
			        _readoutWidget.newText("Alpha Ceti IV\nSize: 4.2352E12\nOrbit: 3.423455\nDes: SuperClass Excelsior\nAnd another thing\nSize: 4 feet\nLength: 345 parsecs");
				}
	        });
	        add(_setReadoutButton);
	        
	        _readoutWidget = new ReadoutWidget();
	        add(_readoutWidget);
	        _readoutWidget.setSize(100, 100);
	        _readoutWidget.setPosition(500, 500);
	        
	        _addReadoutButton = new Button();
	        _addReadoutButton.setText("Add readout text");
	        _addReadoutButton.addCallback(new Runnable() {

				@Override
				public void run() {
			        _readoutWidget.addText("Another line\nfollowed by a nother line");
				}
	        });
	        add(_addReadoutButton);
		}
		
		@Override
		protected void layout() {
			int cury = getParent().getHeight()/2;
	        _addbutton.adjustSize();
			_addbutton.setPosition((getParent().getWidth()-_addbutton.getWidth())/2, cury);
			cury+= _addbutton.getHeight();
			
	        _delbutton.adjustSize();
			_delbutton.setPosition((getParent().getWidth()-_delbutton.getWidth())/2, cury);
			cury+= _delbutton.getHeight();
			
	        _setReadoutButton.adjustSize();
			_setReadoutButton.setPosition((getParent().getWidth()-_setReadoutButton.getWidth())/2, cury);
			cury+= _setReadoutButton.getHeight();
			
	        _addReadoutButton.adjustSize();
			_addReadoutButton.setPosition((getParent().getWidth()-_addReadoutButton.getWidth())/2, cury);			
			cury+= _addReadoutButton.getHeight();
		}
	}
	
	public void run() {

		// Set up the projection matrix
		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		_projection.fromPerspective(60.0f, aspect, 0.1f, 10.0f);
		_shaderProgramManager.setShaderProgramMatrixes(_projection.getProjectionMatrixFloatBuffer(), null);
		
		_camera.setRadius(2.0f);
		_camera.setUpVector(Vector3f.UNIT_Y);
		_camera.setTarget(Vector3f.ZERO);
		_camera.lookAtTarget();
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
        while(!Display.isCloseRequested()) {
    		double time = System.currentTimeMillis();
    		time = System.nanoTime()/1000000;
    		float a = (float) (time/2000 % (FastMath.TWO_PI));
        	// Do the graphics stuff
        	GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        	GL11.glScissor(0, 0, Display.getWidth(), Display.getHeight());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    		Vector3f mouseVector = new Vector3f(Mouse.getX(),Mouse.getY(),0);
    		float dx = Mouse.getDX();
    		float dy = Mouse.getDY();
    		float dr = Mouse.getDWheel();
    		
    		boolean leftButtonDown = Mouse.isButtonDown(0);
    		boolean rightButtonDown = Mouse.isButtonDown(1);
    		boolean middleButtonDown = Mouse.isButtonDown(2);
    		
    		_camera.addRadius(dr/10000f);
    		if (rightButtonDown) {
    			//Mouse.setGrabbed(true);
    			_camera.addHorizontalRotationAboutTarget(-dx/400f);
    			_camera.addVerticalRotationAboutTarget(-dy/400f);
    		} else {
    			//Mouse.setGrabbed(false);
    		}
    		_camera.lookAtTarget();

    		_shaderProgramManager.setShaderProgramMatrixes(null, _camera.getViewMatrixFloatBuffer());

    		for (int i =0; i < 2; i++) {
	    		float scale = FastMath.sin(a)+1;
	    		_axis.setScale(1f,1f,1f);
	    		_axis.draw();
	    		
	    		_wireSphere.setScale(scale/2, scale/2, scale/2);
	    		_wireSphere.setColor(1f, 1f, 1f, 1f);
	    		_wireSphere.draw();
	    		
	    		glEnable(GL11.GL_BLEND);
	    		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
	    		glDisable(GL_DEPTH_TEST);
	
	    		_glowSphere.setColor(0.1f, 0.1f, 0.2f, 1.0f);
	    		_glowSphere.setTranslation(Vector3f.ZERO);
	    		_glowSphere.draw();
	    		_glowSphere.setTranslation(Vector3f.UNIT_X);
	    		_glowSphere.draw();
	    		_glowSphere.setTranslation(Vector3f.UNIT_Y);
	    		_glowSphere.draw();
	    		_glowSphere.setTranslation(Vector3f.UNIT_Z);
	    		_glowSphere.setScale(scale,scale,scale);
	    		_glowSphere.setColor(1.0f, 0f, 0f, 1.0f);
	    		_glowSphere.draw();
	    		glEnable(GL_DEPTH_TEST);
	    		glDisable(GL11.GL_BLEND);
	    		
	    		if (i == 0) {
		        	GL11.glViewport(100, 100, 200, 500);
		        	GL11.glScissor(100, 100, 200, 500);
		            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	    		}
    		}
    		
        	GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        	GL11.glScissor(0, 0, Display.getWidth(), Display.getHeight());

        	// Find out the pixel coords of this node
    		Vector3f sizeVector = new Vector3f(Vector3f.UNIT_X);
			_camera.applyViewMatrix(sizeVector);
    		sizeVector.addLocal(0.1f, 0f, 0f);
			_projection.applyProjectionMatrix(sizeVector);
			_projection.translateToScreenCoordinates(sizeVector);
    		Vector3f tempVector = new Vector3f(Vector3f.UNIT_X);
			_camera.applyViewMatrix(tempVector);
			_projection.applyProjectionMatrix(tempVector);
			_projection.translateToScreenCoordinates(tempVector);
			_rootWidget._targetWidget.setTargetPosition((int)tempVector.x, Display.getHeight() - (int)tempVector.y);
			// tempVector.setZ(0);
			// sizeVector.setZ(0);
			int size = (int)tempVector.distance(sizeVector);
			_rootWidget._targetWidget.setTargetSize(size); // NOT RIGHT
			_rootWidget._readoutWidget.setPosition((int)tempVector.x+ size /2 + 5, Display.getHeight() - (int)tempVector.y - size/2);

    		_gui.update();

    		Display.update();
        }
        Display.destroy();		
	}

}
