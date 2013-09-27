package ca.mcpnet.demurrage.GameClient.GL;

import java.nio.FloatBuffer;

import ca.mcpnet.demurrage.GameClient.jme.FastMath;
import ca.mcpnet.demurrage.GameClient.jme.Matrix4f;
import ca.mcpnet.demurrage.GameClient.jme.Quaternion;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;

public class Camera {
	private Vector3f _position;
	private Quaternion _orientation;

	private Vector3f _worldUpVector;
	private Vector3f _target;
	private float _radius;
	private float _horizRads;
	private float _vertRads;
	private Quaternion _rotAboutTarget;
	private Vector3f _posRelativeToTarget;

	protected Matrix4f _viewMatrix;
	protected FloatBuffer _viewMatrixFloatBuffer;
	
	public Camera() {
		_position = new Vector3f(0,0,0);
		_orientation = new Quaternion();
		
		_worldUpVector = new Vector3f();
		_target = new Vector3f();
		_radius = 1;
		_horizRads = 0;
		_vertRads = 0;
		_rotAboutTarget = new Quaternion();
		_posRelativeToTarget = new Vector3f();

		_viewMatrix = new Matrix4f();
		_viewMatrixFloatBuffer = _viewMatrix.toFloatBuffer();
		
	}
	
    /**
     * <code>getDirection</code> retrieves the direction vector the camera is
     * facing.
     *
     * @return the direction the camera is facing.
     * @see Camera#getDirection()
     */
    public Vector3f getDirection() {
        return _orientation.getRotationColumn(2);
    }

    /**
     * <code>getLeft</code> retrieves the left axis of the camera.
     *
     * @return the left axis of the camera.
     * @see Camera#getLeft()
     */
    public Vector3f getLeft() {
        return _orientation.getRotationColumn(0);
    }

    /**
     * <code>getUp</code> retrieves the up axis of the camera.
     *
     * @return the up axis of the camera.
     * @see Camera#getUp()
     */
    public Vector3f getUp() {
        return _orientation.getRotationColumn(1);
    }

    public void setPosition(float x, float y, float z) {
    	_position.set(x, y, z);
    	recalcViewMatrix();
    }
    
    public void setUpVector(Vector3f worldUpVector) {
    	_worldUpVector.set(worldUpVector);
    	_worldUpVector.normalizeLocal();
    }
    
    public void setTarget(Vector3f target) {
    	_target.set(target);
    }
    
    public void setTarget(float x, float y, float z) {
    	_target.set(x, y, z);
    }

    public void setRadius(float radius) {
    	_radius = radius;
	}

    public void addRadius(float dr) {
		_radius += dr;
		if (_radius <= 0)
			_radius = 0.01f;
	}

    public void addHorizontalRotationAboutTarget(float rads) {
    	_horizRads += rads;
    	_horizRads = _horizRads % FastMath.TWO_PI;
    	recalcRotationAboutTargetMatrix();
    }
    
    public void addVerticalRotationAboutTarget(float rads) {
    	_vertRads += rads;
    	if (_vertRads > FastMath.HALF_PI)
    		_vertRads = FastMath.HALF_PI - 0.01f;
    	if (_vertRads < -FastMath.HALF_PI)
    		_vertRads = FastMath.HALF_PI + 0.01f;
    	recalcRotationAboutTargetMatrix();
    }
    
    public void recalcRotationAboutTargetMatrix() {
    	// _rotAboutTarget.fromAngleNormalAxis(_horizRads, _worldUpVector);
    	_rotAboutTarget.fromAngles(_vertRads, _horizRads, 0.0f);
    }
    
    public void lookAtTarget() {
    	// First we adjust the relative position based on the parameters
    	_posRelativeToTarget.set(Vector3f.UNIT_Z);
    	_posRelativeToTarget.multLocal(_radius);
    	_rotAboutTarget.multLocal(_posRelativeToTarget);
    	_position.set(_target).addLocal(_posRelativeToTarget);
    	
    	// Now we figure out direction and stuff 
        Vector3f newDirection = new Vector3f();
        Vector3f newUp = new Vector3f();
        Vector3f newLeft = new Vector3f();

        newDirection.set(_target).subtractLocal(_position).normalizeLocal();

        newUp.set(_worldUpVector); // _worldUpVector is already normalized
        if (newUp.equals(Vector3f.ZERO)) {
            newUp.set(Vector3f.UNIT_Y);
        }

        newLeft.set(newUp).crossLocal(newDirection).normalizeLocal();
        if (newLeft.equals(Vector3f.ZERO)) {
            if (newDirection.x != 0) {
                newLeft.set(newDirection.y, -newDirection.x, 0f);
            } else {
                newLeft.set(0f, newDirection.z, -newDirection.y);
            }
        }

        newUp.set(newDirection).crossLocal(newLeft).normalizeLocal();

        _orientation.fromAxes(newLeft, newUp, newDirection);
        _orientation.normalizeLocal();

        recalcViewMatrix();
    }

    private void recalcViewMatrix() {
	    _viewMatrix.fromFrame(_position, getDirection(), getUp(), getLeft());
		_viewMatrixFloatBuffer.rewind();
		_viewMatrix.fillFloatBuffer(_viewMatrixFloatBuffer);
    }
    
    public FloatBuffer getViewMatrixFloatBuffer() {
    	_viewMatrixFloatBuffer.rewind();
    	return _viewMatrixFloatBuffer;
    }

	public void applyViewMatrix(Vector3f tempVector) {
		_viewMatrix.mult(tempVector, tempVector);
	}

	/*
	 * These are members and methods for dynamic camera stuff
	 */
	
	// Target Transition vars
	private boolean _targetTransEnabled = false;
	private Vector3f _srcTarget;
	private Vector3f _dstTarget;
	private long _targetTransDuration;
	private long _targetTransStartTime;

	public void startTargetTransition(Vector3f dstTarget, long targetTransDuration) {
		_srcTarget = _target.clone();
		_dstTarget = dstTarget;
		_targetTransDuration = targetTransDuration;
		_targetTransStartTime = System.currentTimeMillis();
		_targetTransEnabled = true;
	}
	
	// Radius Transition vars
	private boolean _radiusTransEnabled = false;
	private float _srcRadius;
	private float _dstRadius;
	private long _radiusTransDuration;
	private long _radiusTransStartTime;

	public void startRadiusTransition(float dstRadius, long radiusTransDuration) {
		_srcRadius = _radius;
		_dstRadius = dstRadius;
		_radiusTransDuration = radiusTransDuration;
		_radiusTransStartTime = System.currentTimeMillis();
		_radiusTransEnabled = true;
	}
	
	public void update() {
		long curtime = System.currentTimeMillis();
		if (_targetTransEnabled) {
			float t = (float) (curtime - _targetTransStartTime) / _targetTransDuration;
			t = 1/(1+FastMath.exp(4-t*8));
			_target.interpolate(_srcTarget, _dstTarget, t);
			if (t >= 1.0) {
				_targetTransEnabled = false;
				_target.set(_dstTarget);
			}
		}
		if (_radiusTransEnabled) {
			float t = (float) (curtime - _radiusTransStartTime) / _radiusTransDuration;
			t = 1/(1+FastMath.exp(4-t*8));
			_radius = _srcRadius + (_dstRadius - _srcRadius)*t;
			if (t >= 1.0) {
				_radiusTransEnabled = false;
				_radius = _dstRadius;
			}
			
		}
		lookAtTarget();
	}

}
