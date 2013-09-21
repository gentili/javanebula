package ca.mcpnet.demurrage.GameEngine.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;

import ca.mcpnet.demurrage.GameEngine.GameClient.ShaderProgramManager;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.Vector4f;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameEngine.GameClient.jme.VertexBuffer.Usage;

public class Axis extends Renderable {

	static private ShaderProgramManager _shaderProgramManager;
	
	static public void setShaderProgramManager(ShaderProgramManager spm) {
		_shaderProgramManager = spm;
	}

	static private VertexBuffer _vbindex;
	static private VertexBuffer _vbpos;
	static private VertexBuffer _vbcolor;
	static private boolean _INIT = false;

	static private int _vertexAttrIndex_pos;
	static private int _vertexAttrIndex_color;

	public Axis() {
		super(_shaderProgramManager.simpleIndexedColorShaderProgram());
		
		// ONE TIME INIT
		if (_INIT) {
			return;
		}
		_INIT = true;

		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_vertexAttrIndex_color = _shaderProgram.getAttribLocation("color");

		Vector<Integer> indexArray = new Vector<Integer>();
		Vector<Vector3f> vertexArray = new Vector<Vector3f>();
		Vector<Vector4f> colorArray = new Vector<Vector4f>();
		
		// X AXIS
		vertexArray.add(Vector3f.ZERO);
		vertexArray.add(Vector3f.UNIT_X);
		indexArray.add(0);
		indexArray.add(1);
		colorArray.add(new Vector4f(0.1f, 0f, 0f, 1f));
		colorArray.add(new Vector4f(  1f, 0f, 0f, 1f));
		// Y AXIS
		vertexArray.add(Vector3f.ZERO);
		vertexArray.add(Vector3f.UNIT_Y);
		indexArray.add(2);
		indexArray.add(3);		
		colorArray.add(new Vector4f(0f, 0.1f, 0f, 1f));
		colorArray.add(new Vector4f(0f,   1f, 0f, 1f));
		// Z AXIS
		vertexArray.add(Vector3f.ZERO);
		vertexArray.add(Vector3f.UNIT_Z);
		indexArray.add(4);
		indexArray.add(5);		
		colorArray.add(new Vector4f(0f, 0f, 0.1f, 1f));
		colorArray.add(new Vector4f(0f, 0f,   1f, 1f));
		
		///////////////
		// GLSL setup
		///////////////

        // Set up the index VBO
        int[] ib = ArrayUtils.toPrimitive(indexArray.toArray(new Integer[indexArray.size()]));
		_vbindex = new VertexBuffer(VertexBuffer.Type.Index);
		_vbindex.setupData(Usage.Static, 1, Format.UnsignedInt, BufferUtils.createIntBuffer(ib));
		
		// Set up the position VBO
		Vector3f[] va = vertexArray.toArray(new Vector3f[vertexArray.size()]);
        _vbpos = new VertexBuffer(VertexBuffer.Type.Position);
        _vbpos.setupData(Usage.Static, 3, Format.Float, BufferUtils.createFloatBuffer(va));

        // Set up the color VBO
		Vector4f[] ca = colorArray.toArray(new Vector4f[colorArray.size()]);
        _vbcolor = new VertexBuffer(VertexBuffer.Type.Color);
        _vbcolor.setupData(Usage.Static, 4, Format.Float, BufferUtils.createFloatBuffer(ca));
        
		// Send vbos to GPU
		_vbindex.updateBufferDataOnGPU();
		_vbpos.updateBufferDataOnGPU();
		_vbcolor.updateBufferDataOnGPU();
	}

	@Override
	public void draw() {
        // Set the modelMatrix uniform
        _modelMatrixFloatBuffer.rewind();
        // GL20.glUseProgram(_shaderProgram.getID()); <-- This is implied by the setModelMatrix() call	
        _shaderProgram.setModelMatrix(_modelMatrixFloatBuffer);

		// Set up the position array
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbpos.getId());
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glVertexAttribPointer(_vertexAttrIndex_pos, 3, GL_FLOAT, false, 0, 0);

		// Set up the color array
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbcolor.getId());
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_color);
		GL20.glVertexAttribPointer(_vertexAttrIndex_color, 4, GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Bind the indexes
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _vbindex.getId());
		
		GL11.glDrawElements(GL11.GL_LINES, _vbindex.getNumElements(), GL11.GL_UNSIGNED_INT, 0L); // Motherfucker!  Has to be GL_UNSIGNED_INT
		Util.checkGLError();

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // Clear binding
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_pos);
		// GL20.glDisableVertexAttribArray(_vertexAttrIndex_color);
        GL20.glUseProgram(0);		

	}

}
