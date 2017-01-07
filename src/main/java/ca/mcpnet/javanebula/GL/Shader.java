package ca.mcpnet.javanebula.GL;

import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import ca.mcpnet.javanebula.jme.BufferUtils;

public class Shader {

	private int _id;

	public Shader(int type, String resource) throws IOException {
		// Load in the source
		
		InputStream istream = Shader.class.getResourceAsStream(resource);
		if (istream == null) {
			throw new RuntimeException("Could not find resource "+resource);
		}
		String src;
		try {
			src = IOUtils.toString(istream);
		} 
		catch (Exception e) {
			RuntimeException e2 = new RuntimeException("Failed loading "+resource,e);
			throw e2;
		}
		
		// Now try and compile it
		_id = GL20.glCreateShader(type);
        if (_id <= 0) {
            throw new RuntimeException("Invalid ID (" + _id + ") received when trying to create shader.");
        }
		ByteBuffer srcbuf = BufferUtils.createByteBuffer(src);
		GL20.glShaderSource(_id, srcbuf);
		GL20.glCompileShader(_id);
		boolean compileOK = GL20.glGetShaderi(_id, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;

		String errString = null;
		int length = GL20.glGetShaderi(_id, GL20.GL_INFO_LOG_LENGTH);
        if (length > 3) {
            // get infos
            ByteBuffer logBuf = BufferUtils.createByteBuffer(length);
            glGetShaderInfoLog(_id, null, logBuf);
            byte[] logBytes = new byte[length];
            logBuf.get(logBytes, 0, length);
            // convert to string, etc
            errString = new String(logBytes).trim();
        }
        if (!compileOK) {
        	throw new RuntimeException("Shader Compile Error in '"+resource+"'\n"+errString);
        }
	}

	public int getID() {
		return _id;
	}
}
