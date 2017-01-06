package ca.mcpnet.demurrage.GameClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.Nebula;
import ca.mcpnet.demurrage.GameClient.GL.PixellationFBO;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Matrix4f;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;


public class MainMenuState extends ClientState {

	private Camera _camera;
	private Axis _axis;
	private Nebula _nebula;
	private PixellationFBO _pixellationFBO;
	
	MainMenuState(GameClient gc) {
		super(gc);
		
		_axis = new Axis();
		_nebula = new Nebula();
		_pixellationFBO = new PixellationFBO(4);
		
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

		_gameClient.getGameClientRootPane().setConnectFocus();

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
		*/
        
        _pixellationFBO.begin();
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
	public String getUsername() {
		return this._gameClient.getGameClientRootPane()._settingsdialog.getUsername();
	}
	public String getPassword() {
		return this._gameClient.getGameClientRootPane()._settingsdialog.getPassword();
	}

	/*
	public void appendToLogPane(String str) {
		this._mainMenuRootPane.appendToLogPane(str);
	}
	
	public void gotoSettingsDialog() {
		this._mainMenuRootPane.gotoSettingsDialog();
	}
	
	public void gotoMainMenu() {
		this._mainMenuRootPane.gotoMainMenu();
	}
	
	public void gotoPopup(String message) {
		this._mainMenuRootPane.gotoPopup(message);
	}
	 */
}
