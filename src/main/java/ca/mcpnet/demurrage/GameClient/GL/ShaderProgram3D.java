package ca.mcpnet.demurrage.GameClient.GL;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL20;

public class ShaderProgram3D extends ShaderProgram {


	int _uniformIndex_modelMatrix;
	int _uniformIndex_viewMatrix;
	int _uniformIndex_projectionMatrix;

	public ShaderProgram3D() {
		_id = GL20.glCreateProgram();
        if (_id <= 0) {
            throw new RuntimeException("Invalid ID (" + _id + ") received when trying to create shader program.");
        }
        _shaders = new ArrayList<Shader>();
	}

	@Override
	public void attachAndLink() {
		super.attachAndLink();
	    _uniformIndex_modelMatrix = getUniformLocation("modelMatrix");
	    _uniformIndex_viewMatrix = getUniformLocation("viewMatrix");
	    _uniformIndex_projectionMatrix = getUniformLocation("projectionMatrix");
	}

	public void setViewMatrix(FloatBuffer viewMatrixFloatBuffer) {
        GL20.glUseProgram(getID());
        GL20.glUniformMatrix4(_uniformIndex_viewMatrix,true,viewMatrixFloatBuffer);
	}

	public void setModelMatrix(FloatBuffer modelMatrixFloatBuffer) {
        GL20.glUseProgram(getID());
        GL20.glUniformMatrix4(_uniformIndex_modelMatrix,true,modelMatrixFloatBuffer);
	}

	public void setProjectionMatrix(FloatBuffer projectionMatrixFloatBuffer) {
        GL20.glUseProgram(getID());
        GL20.glUniformMatrix4(_uniformIndex_projectionMatrix,true,projectionMatrixFloatBuffer);
	}
}
