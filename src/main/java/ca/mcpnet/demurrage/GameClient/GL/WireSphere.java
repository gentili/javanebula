package ca.mcpnet.demurrage.GameClient.GL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import ca.mcpnet.demurrage.GameClient.ShaderProgramManager;
import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Usage;

public class WireSphere extends Renderable {
	
	static private ShaderProgramManager _shaderProgramManager;
	
	static public void setShaderProgramManager(ShaderProgramManager spm) {
		_shaderProgramManager = spm;
	}
	
	static private VertexBuffer _vbindex;
	static private VertexBuffer _vbpos;
	static private boolean _INIT = false;

	static private int _vertexAttrIndex_pos;
	static private int _vertexAttrIndex_normal;
	static private int _uniformIndex_color;
	
	public WireSphere() {
		super(_shaderProgramManager.cameraIsLightShaderProgram());
		
		// ONE TIME INIT
		if (_INIT) {
			return;
		}
		_INIT = true;

		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_vertexAttrIndex_normal = _shaderProgram.getAttribLocation("normal");
		_uniformIndex_color = _shaderProgram.getUniformLocation("color");

		int grades = 20;
		Vector3f vertex = new Vector3f();
		Vector<Vector3f> vertexArray = new Vector<Vector3f>();
		Vector<Integer> indexArray = new Vector<Integer>();
		int curindex = 0;
		for (float theta = 0; theta < 2*Math.PI; theta += 2*Math.PI/grades) {
			vertex.x = FastMath.sin(theta);
			vertex.y = 0.0f;
			vertex.z = FastMath.cos(theta);
			vertexArray.add(vertex.clone());
			indexArray.add(curindex++);
			indexArray.add(curindex);
		}
		// Fix last index
		indexArray.set(indexArray.size()-1, curindex - grades);
		for (int direction = -1; direction < 2; direction += 2) {
			for (float phi = FastMath.PI/(grades); phi < Math.PI/2; phi += Math.PI/(grades)) {
				for (float theta = 0; theta < 2*Math.PI; theta += 2*Math.PI/grades) {
					vertex.x = FastMath.sin(theta)*FastMath.cos(phi);
					vertex.y = FastMath.sin(phi)*direction;
					vertex.z = FastMath.cos(theta)*FastMath.cos(phi);
					vertexArray.add(vertex.clone());
					indexArray.add(curindex++);
					indexArray.add(curindex);
				}
				indexArray.set(indexArray.size()-1, curindex - grades);
			}
		}
		// Bottom half of verticals
		for (int i = 0; i < grades/2; i++) {
			for (int j = 0; j < grades; j++) {
				indexArray.add(i*grades+j);
				indexArray.add(i*grades+grades+j);				
			}
		}
		// Central vertical strip
		for (int j = 0; j < grades; j++) {
			indexArray.add(j);
			indexArray.add(grades*grades/2+grades+j);				
		}		
		// Top half of verticals
		for (int i = grades/2+1; i < grades; i++) {
			for (int j = 0; j < grades; j++) {
				indexArray.add(i*grades+j);
				indexArray.add(i*grades+grades+j);				
			}
		}
		
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

	public void draw() {
        // Set the modelMatrix uniform
        _modelMatrixFloatBuffer.rewind();
        // GL20.glUseProgram(_shaderProgram.getID()); <-- This is implied by the setModelMatrix() call	
        _shaderProgram.setModelMatrix(_modelMatrixFloatBuffer);

        // Set the color uniform
		GL20.glUniform4f(_uniformIndex_color, _color.getX(), _color.getY(), _color.getZ(), _color.getW());

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbpos.getId());

		// Set up the normal array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_normal);
		GL20.glVertexAttribPointer(_vertexAttrIndex_normal, 3, GL_FLOAT, false, 0, 0);

		// Set up the position array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glVertexAttribPointer(_vertexAttrIndex_pos, 3, GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Clear binding
		
		// Bind the indexes
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _vbindex.getId());
		
		GL11.glDrawElements(GL11.GL_LINES, _vbindex.getNumElements(), GL11.GL_UNSIGNED_INT, 0L); // Motherfucker!  Has to be GL_UNSIGNED_INT

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // Clear binding
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_normal);
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_pos);
        GL20.glUseProgram(0);		
	}

}
