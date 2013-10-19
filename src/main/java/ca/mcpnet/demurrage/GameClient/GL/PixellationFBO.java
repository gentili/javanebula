package ca.mcpnet.demurrage.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.FloatBuffer;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import ca.mcpnet.demurrage.GameClient.ShaderProgramManager;
import ca.mcpnet.demurrage.GameClient.jme.BufferUtils;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Format;
import ca.mcpnet.demurrage.GameClient.jme.VertexBuffer.Usage;

public class PixellationFBO {
	static private ShaderProgramManager _shaderProgramManager;

	public static void setShaderProgramManager(
			ShaderProgramManager shaderProgramManager) {
		_shaderProgramManager = shaderProgramManager; 
	}

	private int FBOid;
	private ShaderProgram2D _shaderProgram;
	private int _texBufId;
	private VertexBuffer _vbpos;
	private VertexBuffer _vbindex;
	private int _vertexAttrIndex_pos;
	private int _vertexAttrIndex_tex;

	public PixellationFBO() {
		// Set up FrameBuffer and attached buffers
		// Setup up texture buffer
		_texBufId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _texBufId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, Display.getWidth()/2, Display.getHeight()/2, 
				0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		// Setup depth buffer
		int RBdepthid = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, RBdepthid);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, Display.getWidth()/2, Display.getHeight()/2);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

		// Setup the frame buffer object
		FBOid = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBOid);
		
		// Attach Color texture
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, 
				GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, _texBufId, 0);
		
		// Attach Depth buffer
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, RBdepthid);
		
		// Verify setup
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != 
				GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebufferobject incomplete!");
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		// Set up shader and square polygon
		
		_shaderProgram = _shaderProgramManager.passThroughShaderProgram();
		
		// Make a square out of triangles
		TexturedVertex vertex = new TexturedVertex();
		Vector<float[]> vertexArray = new Vector<float[]>();
		Vector<Integer> indexArray = new Vector<Integer>();
		Integer curindex = 0;

		// First half
		vertex.setXYZ(-1f, 1f, 0f);
		vertex.setST(0f, 1f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);
		
		vertex.setXYZ(1f, 1f, 0f);
		vertex.setST(1f, 1f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);
		
		vertex.setXYZ(1f, -1f, 0f);
		vertex.setST(1f, 0f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);
	
		// Second half
		vertex.setXYZ(-1f, 1f, 0f);
		vertex.setST(0f, 1f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);
		
		vertex.setXYZ(-1f, -1f, 0f);
		vertex.setST(0f, 0f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);
		
		vertex.setXYZ(1f, -1f, 0f);
		vertex.setST(1f, 0f);
		vertexArray.add(vertex.getElements());
		indexArray.add(curindex++);

		
		///////////////
		// GLSL setup
		///////////////
		_vertexAttrIndex_pos = _shaderProgram.getAttribLocation("pos");
		_vertexAttrIndex_tex = _shaderProgram.getAttribLocation("tex");

		// Set up the position VBO
		float[][] va = vertexArray.toArray(new float[vertexArray.size()][]);
		FloatBuffer vafb = BufferUtils.createFloatBuffer(va.length * TexturedVertex.elementCount);
		for (int i = 0; i < va.length; i++)
			vafb.put(va[i]);
        _vbpos = new VertexBuffer(VertexBuffer.Type.Position);
        _vbpos.setupData(Usage.Static, TexturedVertex.elementCount, Format.Float, vafb);
        System.out.println(TexturedVertex.elementCount);
        
        // Set up the index VBO
        int[] ib = ArrayUtils.toPrimitive(indexArray.toArray(new Integer[indexArray.size()]));
		_vbindex = new VertexBuffer(VertexBuffer.Type.Index);
		_vbindex.setupData(Usage.Static, 1, Format.UnsignedInt, BufferUtils.createIntBuffer(ib));
		
		// Send vbos to GPU
		_vbpos.updateBufferDataOnGPU();
		_vbindex.updateBufferDataOnGPU();
	}

	public void begin() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBOid);
    	GL11.glViewport(0, 0, Display.getWidth()/2, Display.getHeight()/2);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void end() {
		/*
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, FBOid);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBlitFramebuffer(0, 0, Display.getWidth()/2, Display.getHeight()/2, 
				0, 0, Display.getWidth(), Display.getHeight(), 
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		*/
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    	GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    	
    	// Texture render
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
    	GL20.glUseProgram(_shaderProgram.getID());
    	
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, _texBufId);
    	
    	// Bind the array buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbpos.getId());

		// Set up the position array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glVertexAttribPointer(_vertexAttrIndex_pos, TexturedVertex.positionElementCount, GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.positionByteOffset);

		// Set up the texture array
		GL20.glEnableVertexAttribArray(_vertexAttrIndex_tex);
		GL20.glVertexAttribPointer(_vertexAttrIndex_tex, TexturedVertex.textureElementCount, GL_FLOAT, 
				false, TexturedVertex.stride, TexturedVertex.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Clear binding
		
		// Bind the indexes
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _vbindex.getId());
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, _vbindex.getNumElements(), GL11.GL_UNSIGNED_INT, 0L); // Motherfucker!  Has to be GL_UNSIGNED_INT

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // Clear binding
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_pos);
		GL20.glDisableVertexAttribArray(_vertexAttrIndex_tex);
        GL20.glUseProgram(0);		

	}

}
