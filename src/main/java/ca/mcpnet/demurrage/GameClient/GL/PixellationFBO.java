package ca.mcpnet.demurrage.GameClient.GL;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class PixellationFBO {
	// static private ShaderProgramManager _shaderProgramManager;
	private int FBOid;

	public PixellationFBO() {
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
			throw new RuntimeException("FBOs not supported");
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_blit)
			throw new RuntimeException("FBO blit not supported");
				
		// Setup up color buffer
		int TEXcolorid = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXcolorid);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, Display.getWidth()/2, Display.getHeight()/2, 
				0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
		
		// Setup depth buffer
		int RBdepthid = ARBFramebufferObject.glGenRenderbuffers();
		ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, RBdepthid);
		ARBFramebufferObject.glRenderbufferStorage(ARBFramebufferObject.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, Display.getWidth()/2, Display.getHeight()/2);
		ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, 0);

		// Setup the frame buffer object
		FBOid = ARBFramebufferObject.glGenFramebuffers();
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, FBOid);
		
		// Attach Color texture
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, 
				ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, TEXcolorid, 0);
		
		// Attach Depth buffer
		ARBFramebufferObject.glFramebufferRenderbuffer(ARBFramebufferObject.GL_FRAMEBUFFER,
				ARBFramebufferObject.GL_DEPTH_ATTACHMENT, ARBFramebufferObject.GL_RENDERBUFFER, RBdepthid);
		
		// Verify setup
		if (ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER) != 
				ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebufferobject incomplete!");
		}
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
	}

	public void begin() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, FBOid);
    	GL11.glViewport(0, 0, Display.getWidth()/2, Display.getHeight()/2);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void end() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, FBOid);
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
		ARBFramebufferObject.glBlitFramebuffer(0, 0, Display.getWidth()/2, Display.getHeight()/2, 
				0, 0, Display.getWidth(), Display.getHeight(), 
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
    	GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
}
