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
import ca.mcpnet.demurrage.GameClient.jme.Quaternion;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Usage;

public class PointStar extends Renderable {
	
	static private ShaderProgramManager _shaderProgramManager;
	
	static public void setShaderProgramManager(ShaderProgramManager spm) {
		_shaderProgramManager = spm;
	}

	private VertexBuffer _vbindex;
	private VertexBuffer _vbpos;

	private int _vertexAttrIndex_pos;
	private int _uniformIndex_color;
	
	public PointStar(float scale) {
		super (_shaderProgramManager.concursionPointShaderProgram());
		
		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_uniformIndex_color = _shaderProgram.getUniformLocation("color");
		
		Vector3f vertexA = new Vector3f();
		Vector3f vertexB = new Vector3f();
		Vector3f vertexC = new Vector3f();
		Vector<Vector3f> vertexArray = new Vector<Vector3f>();
		Vector<Integer> indexArray = new Vector<Integer>();
		Integer curindex = 0;
		
		vertexA.x = -0.5f*scale;
		vertexA.y = 0.0f;
		vertexA.z = 0.0f;
		
		vertexB.x = 0.5f*scale;
		vertexB.y = 0.0f;
		vertexB.z = 0.0f;
		
		vertexC.x = 0.0f;
		vertexC.y = 1.0f*scale;
		vertexC.z = 0.0f;
	
		Quaternion zeroQ = new Quaternion();
		zeroQ.fromAngleNormalAxis(0, new Vector3f(0.0f,0.0f,1.0f));
		
		Quaternion halfpiQ = new Quaternion();
		halfpiQ.fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(0.0f,0.0f,1.0f));

		Quaternion piQ = new Quaternion();
		piQ.fromAngleNormalAxis(FastMath.PI, new Vector3f(0.0f,0.0f,1.0f));

		Quaternion minushalfpiQ = new Quaternion();
		minushalfpiQ.fromAngleNormalAxis(-FastMath.HALF_PI, new Vector3f(0.0f,0.0f,1.0f));

		for (int i = 0; i < 4; i++) {
			curindex = addTriangle(zeroQ,vertexArray,indexArray,curindex,vertexA,vertexB,vertexC);
			curindex = addTriangle(halfpiQ,vertexArray,indexArray,curindex,vertexA,vertexB,vertexC);
			curindex = addTriangle(piQ,vertexArray,indexArray,curindex,vertexA,vertexB,vertexC);
			curindex = addTriangle(minushalfpiQ,vertexArray,indexArray,curindex,vertexA,vertexB,vertexC);
			vertexA.x *= 0.6f;
			vertexB.x *= 0.6f;
			vertexC.y *= 1.5f;
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

	private int addTriangle(Quaternion Q, Vector<Vector3f> vertexArray,
			Vector<Integer> indexArray, Integer curindex, Vector3f vertexA,
			Vector3f vertexB, Vector3f vertexC) {
		vertexArray.add(Q.mult(vertexA));
		indexArray.add(curindex++);
		vertexArray.add(Q.mult(vertexB));
		indexArray.add(curindex++);
		vertexArray.add(Q.mult(vertexC));
		indexArray.add(curindex++);
		return curindex;
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
