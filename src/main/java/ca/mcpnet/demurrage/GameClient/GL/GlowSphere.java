package ca.mcpnet.demurrage.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import ca.mcpnet.demurrage.GameClient.ShaderProgramManager;
import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.Vector4f;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Usage;

public class GlowSphere extends Renderable {
	
	static private ShaderProgramManager _shaderProgramManager;
	
	static public void setShaderProgramManager(ShaderProgramManager spm) {
		_shaderProgramManager = spm;
	}

	private VertexBuffer _vbindex;
	private VertexBuffer _vbpos;	
	private int _vertexAttrIndex_pos;
	private int _uniformIndex_color;
	
	private float _scale;
		
	public GlowSphere(float scale) {
		super (_shaderProgramManager.glowSphereShaderProgram());
		
		_color = new Vector4f();
		_color.zero();
		_scale = scale;
		
		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_uniformIndex_color = _shaderProgram.getUniformLocation("color");
				
		// Make a square out of triangles
		Vector3f vertex = new Vector3f();
		Vector<Vector3f> vertexArray = new Vector<Vector3f>();
		Vector<Integer> indexArray = new Vector<Integer>();
		Integer curindex = 0;

		// First half
		vertex.x = -0.5f*scale;
		vertex.y =  0.5f*scale;
		vertex.z =  0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);
		
		vertex.x = 0.5f*scale;
		vertex.y = 0.5f*scale;
		vertex.z = 0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);
		
		vertex.x =  0.5f*scale;
		vertex.y = -0.5f*scale;
		vertex.z = 0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);
	
		// Second half
		vertex.x = -0.5f*scale;
		vertex.y =  0.5f*scale;
		vertex.z =  0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);
		
		vertex.x = -0.5f*scale;
		vertex.y = -0.5f*scale;
		vertex.z = 0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);
		
		vertex.x =  0.5f*scale;
		vertex.y = -0.5f*scale;
		vertex.z = 0.0f;
		vertexArray.add(vertex.clone());
		indexArray.add(curindex++);

		
		///////////////
		// GLSL setup
		///////////////

		// Set up the position VBO
		Vector3f[] va = vertexArray.toArray(new Vector3f[vertexArray.size()]);
        _vbpos = new VertexBuffer(VertexBuffer.Type.Position);
        _vbpos.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(va));
        
        // Set up the index VBO
        int[] ib = ArrayUtils.toPrimitive(indexArray.toArray(new Integer[indexArray.size()]));
		_vbindex = new VertexBuffer(VertexBuffer.Type.Index);
		_vbindex.setupData(Usage.Static, 1, Format.UnsignedInt, BufferUtils.createIntBuffer(ib));
		
		// Send vbos to GPU
		_vbpos.updateBufferDataOnGPU();
		_vbindex.updateBufferDataOnGPU();
		
	}

	@Override
	public void setScale(Vector3f tempVector) {
		throw new RuntimeException("Scale can only be set during construction!");
	}

	@Override
	public void setScale(float x, float y, float z) {
		throw new RuntimeException("Scale can only be set during construction!");
	}

	@Override
	public void setScale(float r) {
		throw new RuntimeException("Scale can only be set during construction!");
	}
	
	public float getFloatScale() {
		return _scale;
	}
	
	public void draw() {
		_modelMatrixFloatBuffer.rewind();
        // GL20.glUseProgram(_shaderProgram.getID()); <-- This is implied by the setModelMatrix() call
        _shaderProgram.setModelMatrix(_modelMatrixFloatBuffer);
		// Set the color uniform
		GL20.glUniform4f(_uniformIndex_color, _color.x, _color.y, _color.z, _color.w);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbpos.getId());

		// Set up the position array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glVertexAttribPointer(_vertexAttrIndex_pos, 3, GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Clear binding
		
		// Bind the indexes
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _vbindex.getId());
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, _vbindex.getNumElements(), GL11.GL_UNSIGNED_INT, 0L); // Motherfucker!  Has to be GL_UNSIGNED_INT

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // Clear binding
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_pos);
        GL20.glUseProgram(0);		

	}


}
