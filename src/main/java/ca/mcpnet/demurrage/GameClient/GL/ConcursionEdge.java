package ca.mcpnet.demurrage.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import ca.mcpnet.demurrage.GameClient.ShaderProgramManager;
import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Usage;

public class ConcursionEdge extends Renderable {
	
	static private ShaderProgramManager _shaderProgramManager;
	
	static public void setShaderProgramManager(ShaderProgramManager spm) {
		_shaderProgramManager = spm;
	}

	static private VertexBuffer _vbindex;
	static private VertexBuffer _vbpos;
	private FloatBuffer _vbposfloatbuf;
	static private boolean _INIT = false;

	private int _vertexAttrIndex_pos;
	private int _uniformIndex_color;
	private float _R;
	private float _G;
	private float _B;
	private float _A;
	
	public ConcursionEdge() {
		super(_shaderProgramManager.concursionEdgeShaderProgram());
		
		if (_INIT) {
			return;
		}
		_INIT = true;
		
		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_uniformIndex_color = _shaderProgram.getUniformLocation("color");

		///////////////
		// GLSL setup
		///////////////

		// Set up the position VBO
		float[] vb = {0,0,0,1,0,0}; 
        _vbpos = new VertexBuffer(VertexBuffer.Type.Position);
        _vbposfloatbuf = BufferUtils.createFloatBuffer(vb);
        _vbpos.setupData(Usage.Stream, 3, Format.Float, _vbposfloatbuf);
        
        // Set up the index VBO
        int[] ib = {0,1};
		_vbindex = new VertexBuffer(VertexBuffer.Type.Index);
		_vbindex.setupData(Usage.Static, 1, Format.UnsignedInt, BufferUtils.createIntBuffer(ib));
		
		// Send vbos to GPU
		_vbpos.updateBufferDataOnGPU();
		_vbindex.updateBufferDataOnGPU();

	}
	
	public void setColor(float r, float g, float b, float a) {
		_R = r;
		_G = g;
		_B = b;
		_A = a;
	}
	
	public void setPoints(Vector3f a, Vector3f b) {
		_vbposfloatbuf.rewind();
		_vbposfloatbuf.put(a.x);
		_vbposfloatbuf.put(a.y);
		_vbposfloatbuf.put(a.z);
		_vbposfloatbuf.put(b.x);
		_vbposfloatbuf.put(b.y);
		_vbposfloatbuf.put(b.z);
		_vbpos.updateData(_vbposfloatbuf);
		_vbpos.updateBufferDataOnGPU();
	}

	public void draw() {
		_modelMatrixFloatBuffer.rewind();
        // GL20.glUseProgram(_shaderProgram.getID()); <-- This is implied by the setModelMatrix() call
        _shaderProgram.setModelMatrix(_modelMatrixFloatBuffer);
		// Set the color uniform
		GL20.glUniform4f(_uniformIndex_color, _R, _G, _B, _A);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbpos.getId());

		// Set up the position array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glVertexAttribPointer(_vertexAttrIndex_pos, 3, GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Clear binding
		
		// Bind the indexes
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _vbindex.getId());
		
		GL11.glDrawElements(GL11.GL_LINES, _vbindex.getNumElements(), GL11.GL_UNSIGNED_INT, 0L); // Motherfucker!  Has to be GL_UNSIGNED_INT

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // Clear binding
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_pos);
        GL20.glUseProgram(0);		

	}

}
