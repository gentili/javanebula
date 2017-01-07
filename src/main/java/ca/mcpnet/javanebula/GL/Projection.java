package ca.mcpnet.javanebula.GL;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;

import ca.mcpnet.javanebula.jme.Matrix4f;
import ca.mcpnet.javanebula.jme.Vector3f;

public class Projection {
	
	private Matrix4f _projectionMatrix;
	private FloatBuffer _projectionMatrixFloatBuffer;

	public Projection() {
		_projectionMatrix = new Matrix4f();
		_projectionMatrixFloatBuffer = _projectionMatrix.toFloatBuffer();
	}
	
	public void fromPerspective(float fovy, float aspect, float zNear, float zFar) {
		_projectionMatrix.fromPerspective(fovy, aspect, zNear, zFar);
		_projectionMatrixFloatBuffer.rewind();
		_projectionMatrix.fillFloatBuffer(_projectionMatrixFloatBuffer);
	}
	
	public FloatBuffer getProjectionMatrixFloatBuffer() {
		_projectionMatrixFloatBuffer.rewind();
		return _projectionMatrixFloatBuffer;
	}

	public void applyProjectionMatrix(Vector3f tempVector) {
		float w = _projectionMatrix.multProj(tempVector, tempVector);
		tempVector.multLocal(1.0f/w);
	}
	
	public void translateToScreenCoordinates(Vector3f tempVector) {

		tempVector.x = (tempVector.x + 1.0f) / 2.0f * Display.getWidth();
		tempVector.y = (tempVector.y + 1.0f) / 2.0f * Display.getHeight();
		// tempVector.x += Display.getWidth() / 2;
		// tempVector.y += Display.getHeight() / 2;
	}
}
