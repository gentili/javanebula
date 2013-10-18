package ca.mcpnet.demurrage.GameClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.GlowSphere;
import ca.mcpnet.demurrage.GameClient.GL.Nebula;
import ca.mcpnet.demurrage.GameClient.GL.PixellationFBO;
import ca.mcpnet.demurrage.GameClient.GL.PointStar;
import ca.mcpnet.demurrage.GameClient.GL.WireSphere;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Matrix4f;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.PopupWindow;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.ScrollPane.Fixed;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;


public class MainMenuState extends ClientState {

	private Camera _camera;
	RootPane _mainMenuRootPane;
	private Axis _axis;
	private WireSphere _wiresphere;
	private PointStar _farStar;
	private PointStar _farStarInner;
	private GlowSphere _glowSphere;
	private Nebula _nebula;
	private PixellationFBO _pixellationFBO;
	
	MainMenuState(GameClient gc) {
		super(gc);
		_rootPane = _mainMenuRootPane = new RootPane(gc);
		
		_axis = new Axis();
		_wiresphere = new WireSphere();
		_farStar = new PointStar(0.25f);
		_farStarInner = new PointStar(0.1f);
		_glowSphere = new GlowSphere(1f);
		_nebula = new Nebula();
		_pixellationFBO = new PixellationFBO();
		
		_camera = new Camera();
		_camera.setUpVector(Vector3f.UNIT_Y);
	}
	
	FloatBuffer allocateDirectFloat(float a, float b, float c, float d) {
		FloatBuffer temp = ByteBuffer.allocateDirect(4*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		temp.put(a);
		temp.put(b);
		temp.put(c);
		temp.put(d);
		temp.rewind();
		return temp;
	}

	@Override
	public void onEnterState() {

		// Set up the projection matrix
		Matrix4f projectionMatrix = new Matrix4f();
		float aspect = (float) Display.getWidth()/ (float) Display.getHeight();
		projectionMatrix.fromPerspective(60.0f, aspect, 1.0f, 8.0f);
		_gameClient.getShaderProgramManager().setShaderProgram3DMatrixes(projectionMatrix.toFloatBuffer(), null);

		_gameClient.getGUI().setRootPane(_rootPane);
		_mainMenuRootPane.setLoginFocus();

		_camera.setTarget(Vector3f.ZERO);
		_camera.setUpVector(Vector3f.UNIT_Y);
		_camera.setRadius(5.0f);
		_camera.lookAtTarget();
	}
	
	@Override
	public void onExitState() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
		double time = System.currentTimeMillis();
		time = System.nanoTime()/1000000;
		float a = (float) (time/2000 % (FastMath.TWO_PI));

		// Set up camera and view matrix
		_camera.addHorizontalRotationAboutTarget(0.001f);
		_camera.update();
		
		// Update the View Matrixes
		_gameClient.getShaderProgramManager().setShaderProgram3DMatrixes(null, _camera.getViewMatrixFloatBuffer());

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

        /*
        GL11.glDisable(GL11.GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		// Draw the helper axis
		
		_axis.setRotation(0, 0, 1f, 0);
		_axis.setTranslation(0f,0f,0f);
		_axis.draw();

		// Draw the spheres
		_wiresphere.setRotation(FastMath.sin(a), 0, 1, 0);
		_wiresphere.setTranslation(0f, 0f, 0f);
		_wiresphere.draw();
		*/
        _pixellationFBO.begin();
		// Draw star
		/*
		_farStar.setTranslation(3f, 0.0f, 0f);
		_farStar.setColor(0.1f, 0f, 0.1f, 1f);
		_farStar.draw();
		_farStarInner.setTranslation(3f, 0.0f, 0f);
		_farStarInner.setColor(0.1f, 0.1f, 0.1f, 1f);
		_farStarInner.draw();
		_farStar.setTranslation(-3f, 0.0f, 0f);
		_farStar.setColor(0f, 0f, 0.1f, 1f);
		_farStar.draw();
		_farStarInner.setTranslation(-3f, 0.0f, 0f);
		_farStarInner.setColor(0.1f, 0.1f, 0.1f, 1f);
		_farStarInner.draw();
		*/
		/*
		_glowSphere.setTranslation(-3f, 0.0f, 0f);
		_glowSphere.setColor(0.0f, 0.0f, 0.4f, 1f);
		_glowSphere.draw();
		_glowSphere.setTranslation(3f, 0.0f, 0f);
		_glowSphere.setColor(0.4f, 0.0f, 0.4f, 1f);
		_glowSphere.draw();
		*/
		_nebula.draw();
		_pixellationFBO.end();
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		/*
		 * HOWTO retrieve an openGL matrix and load it into an uniform
		 *
		// MEMBERS
		// private FloatBuffer _viewMatrix;
		// private int _uniformIndex_viewMatrix;

		// CONSTRUCTOR
		// _viewMatrix = BufferUtils.createFloatBuffer(16);
		// _uniformIndex_viewMatrix = _gameClient.cameraIsLightShaderProgram().getUniformLocation("viewMatrix");

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		GLU.gluLookAt(FastMath.sin(a)*5.0f, 0.0f, FastMath.cos(a)*5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		_viewMatrix.rewind();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, _viewMatrix);
		float[] avm = new float[16];
		_viewMatrix.get(avm);
		Matrix4f ivm = new Matrix4f(avm);
		//ivm.invertLocal();
		_viewMatrix.rewind();
		ivm.fillFloatBuffer(_viewMatrix,false);
		_viewMatrix.rewind();
        GL20.glUseProgram(_gameClient.cameraIsLightShaderProgram().getID());
		GL20.glUniformMatrix4(_uniformIndex_viewMatrix,true,_viewMatrix);
		GL20.glUseProgram(0);
		*/

	}
	
	/*
	 * Passthrough functions to the root pane
	 */
	public void appendToLogPane(String str) {
		this._mainMenuRootPane.appendToLogPane(str);
	}
	public String getUsername() {
		return this._mainMenuRootPane._logindialog.getUsername();
	}
	public String getPassword() {
		return this._mainMenuRootPane._logindialog.getPassword();
	}
	
	public void gotoLoginDialog() {
		this._mainMenuRootPane.gotoLoginDialog();
	}
	
	public void gotoMainMenu() {
		this._mainMenuRootPane.gotoMainMenu();
	}
	
	public void gotoPopup(String message) {
		this._mainMenuRootPane.gotoPopup(message);
	}
	
	public static class RootPane extends Widget {
		
		private MainMenuWidget _mainmenuwidget;
		private LoginDialog _logindialog;
		private ScrollPane _logPane;
		private StringBuilder _logBuf;
		private SimpleTextAreaModel _logPaneTextAreaModel;
		private PopupWindow _popup;
		int linecount = 0;

		RootPane(final GameClient gc) {
			setTheme("");
			// Init the logging output pane
			_logPaneTextAreaModel = new SimpleTextAreaModel();
			_logPane = new ScrollPane(new TextArea(_logPaneTextAreaModel));
			_logPane.setTheme("scrollinglogpane");
			_logPane.setFixed(Fixed.HORIZONTAL);
			_logPane.setExpandContentSize(true);
			_logPane.setEnabled(false);
			_logPane.setVisible(true);
			_logBuf = new StringBuilder();
			appendToLogPane("*** DEMURRAGE GAMECLIENT "+GameClient.VERSION+" ***\n\n");
			_logPane.validateLayout();

			add(_logPane);
			// Init the main menu
			_mainmenuwidget = new MainMenuWidget();
			_mainmenuwidget.setExitCallback(
					new Runnable() {
						@Override
						public void run() { 
							gc.terminate();
							}
						}
					);
			_mainmenuwidget.setLoginCallback(new Runnable() {
				@Override
				public void run() {
					gotoLoginDialog();
				}
			});
			
			_mainmenuwidget.setSettingsCallback(new Runnable() {
				@Override
				public void run() {
					gotoPopup("There are no settings!");
				}
			});
			add(_mainmenuwidget);
			
			// Init the login dialog
			_logindialog = new LoginDialog();
			_logindialog.setVisible(false);
			_logindialog.setLoginCallback(new Runnable() {
				@Override
				public void run() {
					_logindialog.setVisible(false);
					String server = _logindialog.getServer();
					appendToLogPane("Connecting to "+server+ "... ");
					gc._concursionServerConnectionProcessor.connect(server, 1234);
				}
			});
			_logindialog.setCancelCallback(new Runnable() {
				@Override
				public void run() {
					gotoMainMenu();
				}
			});
			add(_logindialog);
			
			_popup = new PopupWindow(this);
			_popup.setVisible(false);
			_popup.add(new Label("Something Unexpected Happened!"));
			_popup.setRequestCloseCallback(new Runnable() {
				@Override
				public void run() {
					gotoMainMenu();
				}
			});
			_popup.getOrCreateActionMap();
		}
		
		@Override
		protected void layout() {
			_mainmenuwidget.adjustSize();
			_mainmenuwidget.setPosition((getParent().getWidth()-_mainmenuwidget.getWidth())/2, (getParent().getHeight()-_mainmenuwidget.getHeight())/2);

			_logindialog.adjustSize();
			_logindialog.setPosition((getParent().getWidth()-_logindialog.getWidth())/2, (getParent().getHeight()-_logindialog.getHeight())/2);
			
			_logPane.setSize(Display.getWidth() - 100, Display.getHeight() - 100);
			_logPane.setPosition((getParent().getWidth()-_logPane.getWidth())/2, (getParent().getHeight()-_logPane.getHeight())/2);
			
			_popup.adjustSize();
			_popup.setPosition((getParent().getWidth()-_popup.getWidth())/2, (getParent().getHeight()-_popup.getHeight())/2);
		}

		private void appendToLogPane(String str) {
			_logBuf.append(str);
			_logPaneTextAreaModel.setText(_logBuf.toString());
			_logPane.setScrollPositionY(_logPane.getMaxScrollPosY());
		}
		
		public void setLoginFocus() {
			_mainmenuwidget.setLoginFocus();
		}
		
		public void gotoLoginDialog() {
			_mainmenuwidget.setVisible(false);
			_logindialog.setVisible(true);
			_logindialog._btnLogin.requestKeyboardFocus();
			_popup.setVisible(false);
		}
		
		public void gotoMainMenu() {
			_mainmenuwidget.setVisible(true);
			_logindialog.setVisible(false);
			_popup.setVisible(false);
		}
		
		public void gotoPopup(String message) {
			_mainmenuwidget.setVisible(false);
			_logindialog.setVisible(false);
			Label l = (Label) _popup.getChild(0);
			l.setText(message);
			_popup.openPopupCentered();
		}
	}
}
