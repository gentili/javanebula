package ca.mcpnet.demurrage.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.*;

import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;

public class ShaderProgram {
	Logger _log = Logger.getLogger("ShaderProgram");

	private int _id;
	ArrayList<Shader> _shaders;
	
	private int _uniformIndex_modelMatrix;
	private int _uniformIndex_viewMatrix;
	private int _uniformIndex_projectionMatrix;

	public ShaderProgram() {
		_id = GL20.glCreateProgram();
        if (_id <= 0) {
            throw new RuntimeException("Invalid ID (" + _id + ") received when trying to create shader program.");
        }
        _shaders = new ArrayList<Shader>();
	}

	public void addShader(Shader shader) {
		_shaders.add(shader);
	}
	
	public void attachAndLink() {
		_log.debug("Attaching and Linking ShaderProgram "+_id);
		Iterator<Shader> itr = _shaders.iterator();
		while(itr.hasNext()) {
			Shader shader = itr.next();
			GL20.glAttachShader(_id, shader.getID());		
		}
		GL20.glLinkProgram(_id);
        boolean linkOK = glGetProgrami(_id, GL_LINK_STATUS) == GL_TRUE;

        int length = glGetProgrami(_id, GL_INFO_LOG_LENGTH);
        if (length > 3) {
            // get infos
            ByteBuffer logBuf = BufferUtils.createByteBuffer(length);
            glGetProgramInfoLog(_id, null, logBuf);

            // convert to string, etc
            byte[] logBytes = new byte[length];
            logBuf.get(logBytes, 0, length);
            _log.debug(new String(logBytes));
        }
        if (!linkOK) {
        	throw new RuntimeException("Linking failed in ShaderProgram "+_id);
        }
        
        int attr_count = glGetProgrami(_id, GL_ACTIVE_ATTRIBUTES);
        _log.debug("Active Attributes: "+attr_count);
        for (int i = 0; i < attr_count; i++) {
        	_log.debug("  " + GL20.glGetActiveAttrib(_id, i, 20));
        }
        
        int uni_count = glGetProgrami(_id, GL_ACTIVE_UNIFORMS);
        _log.debug("Active Uniforms: "+uni_count);
        for (int i = 0; i < uni_count; i++) {
        	_log.debug("  " + GL20.glGetActiveUniform(_id, i, 20));
        }
        
        _uniformIndex_modelMatrix = getUniformLocation("modelMatrix");
        _uniformIndex_viewMatrix = getUniformLocation("viewMatrix");
        _uniformIndex_projectionMatrix = getUniformLocation("projectionMatrix");
	}

	public int getAttribLocation(String name) {
		if (_id <= 0) {
			throw new RuntimeException("Attempt to find attribute in invalid shader program");
		}
		int loc = glGetAttribLocation(_id,name);
		if (loc < 0) {
			throw new RuntimeException("No attribute '"+name+"' found in shader program "+_id);
		}
		return loc;
	}

	public int getUniformLocation(String name) {
		if (_id <= 0) {
			throw new RuntimeException("Attempt to find uniform in invalid shader program");
		}
		int loc = glGetUniformLocation(_id,name);
		if (loc < 0) {
			throw new RuntimeException("No uniform '"+name+"' found in shader program "+_id);
		}
		return loc;
	}
	
	public int getID() {
		return _id;
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
