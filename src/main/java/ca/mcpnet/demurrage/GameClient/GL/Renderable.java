package ca.mcpnet.demurrage.GameClient.GL;

import java.nio.FloatBuffer;

import ca.mcpnet.demurrage.GameClient.jme.Matrix4f;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;

public abstract class Renderable {
	
	protected ShaderProgram _shaderProgram;
	
	protected Vector3f _position;
	protected Vector3f _rotationAxis;
	protected float _rotationMag;
	protected Vector3f _scale;
	
	protected Matrix4f _modelMatrix;
	protected FloatBuffer _modelMatrixFloatBuffer;

	protected Renderable(ShaderProgram sp) {
		_shaderProgram = sp;
		
		_position = Vector3f.ZERO.clone();
		_rotationAxis = Vector3f.UNIT_Y.clone();
		_rotationMag = 0;
		_scale = Vector3f.UNIT_XYZ.clone();
		
		_modelMatrix = new Matrix4f();
		_modelMatrixFloatBuffer = _modelMatrix.toFloatBuffer();
	}
		
	public void setTranslation(float x, float y, float z) {
		_position.set(x, y, z);
		recalcModelMatrix();
	}

	public void setTranslation(Vector3f tempVector) {
		_position.set(tempVector);
		recalcModelMatrix();
	}

	public void setRotation(float rad, float x, float y, float z) {
		_rotationMag = rad;
		_rotationAxis.set(x, y, z);
		_rotationAxis.normalizeLocal();
		recalcModelMatrix();
	}
	
	public void setScale(Vector3f tempVector) {
		_scale.set(tempVector);
		recalcModelMatrix();
	}
	
	public void setScale(float x, float y, float z) {
		_scale.set(x, y, z);
		recalcModelMatrix();
	}
	
	public void setScale(float r) {
		_scale.set(r,r,r);
	}

	private void recalcModelMatrix() {
		_modelMatrix.fromAngleNormalAxis(_rotationMag, _rotationAxis);
		_modelMatrix.scale(_scale);
		_modelMatrix.setTranslation(_position);
		_modelMatrixFloatBuffer.rewind();
		_modelMatrix.fillFloatBuffer(_modelMatrixFloatBuffer);
	}
	
	public Vector3f getTranslation() {
		return _position;
	}
	
	public abstract void draw();
}