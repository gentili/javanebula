package ca.mcpnet.demurrage.GameClient;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
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

	private Axis _axis;
	private WireSphere[] _wiresphere;
	private Camera _camera;
	RootPane _mainMenuRootPane;
	
	MainMenuState(GameClient gc) {
		super(gc);
		_rootPane = _mainMenuRootPane = new RootPane(gc);
		
		_axis = new Axis();
		_wiresphere = new WireSphere[3];
		_wiresphere[0] = new WireSphere();
		_wiresphere[1] = new WireSphere();
		_wiresphere[2] = new WireSphere();
		
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
		projectionMatrix.fromPerspective(60.0f, aspect, 1.0f, 6.0f);
		_gameClient.getShaderProgramManager().setShaderProgramMatrixes(projectionMatrix.toFloatBuffer(), null);

		glEnable(GL_DEPTH_TEST);

		_gameClient.getGUI().setRootPane(_rootPane);
		_mainMenuRootPane.setLoginFocus();

		_camera.setTarget(Vector3f.ZERO);
		_camera.setRadius(5.0f);
	}
	
	@Override
	public void onExitState() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		double time = System.currentTimeMillis();
		time = System.nanoTime()/1000000;
		float a = (float) (time/2000 % (FastMath.TWO_PI));

		// Set up camera and view matrix
		// _camera.rotateHorizontalAboutTarget(a)*5.0f, 0.0f, FastMath.cos(a)*5.0f);
		// _camera.addHorizontalRotationAboutTarget(0.01f);
		_camera.lookAtTarget();
		
		// Update the View Matrixes
		_gameClient.getShaderProgramManager().setShaderProgramMatrixes(null, _camera.getViewMatrixFloatBuffer());

		// Draw the helper axis
		_axis.setRotation(0, 0, 1f, 0);
		_axis.setTranslation(0f,0f,0f);
		// _axis.draw();
		// Draw the spheres
		_wiresphere[0].setRotation(FastMath.sin(a), 0, 1, 0);
		_wiresphere[0].setTranslation(0f, 0f, 0f);
		_wiresphere[0].draw();
		_wiresphere[1].setRotation(FastMath.sin(a)*2, 0, 1, 0);
		_wiresphere[1].setTranslation(2.0f, (float) (FastMath.sin(a)*2.0), 0f);
		_wiresphere[1].draw();
		_wiresphere[2].setRotation(FastMath.sin(a), 0, 1, 0);
		_wiresphere[2].setTranslation(-2.0f, 0.0f, (float) (FastMath.sin(a)*2.0));
		_wiresphere[2].draw();
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
			appendToLogPane("*** DEMURRAGE GAMECLIENT V1.0 ***\n\n");
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
