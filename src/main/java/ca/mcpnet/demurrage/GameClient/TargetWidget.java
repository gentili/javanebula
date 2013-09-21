package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;

public class TargetWidget extends Widget {
	
	public static final StateKey SNAP = StateKey.get("snap");
    public static final StateKey GROW = StateKey.get("grow");
    public static final StateKey PULSE = StateKey.get("pulse");
	
	private Widget _targetbox;
	private int _targetX;
	private int _targetY;
	private int _targetSize = 30;
	private int _snapSize;

	public TargetWidget() {
		_targetbox = new Widget();
		_targetbox.setTheme("targetbox");
		add(_targetbox);
		
	}

	boolean _validatedSinceLastUpdate = false;
	@Override
	public void validateLayout() {
		if (!_validatedSinceLastUpdate) {
			_validatedSinceLastUpdate = true;
			if (_targetbox.getAnimationState().getAnimationState(SNAP)) {
				invalidateLayoutLocally();
			}
		}
		super.validateLayout();
	}
	
	@Override
	public void layout() {
		int cursize = _targetSize;
		if (_targetbox.getAnimationState().getAnimationState(SNAP)) {
			int elapsed = _targetbox.getAnimationState().getAnimationTime(SNAP);
			if (elapsed > 200) {
				_targetbox.getAnimationState().setAnimationState(SNAP, false);
				_targetbox.getAnimationState().resetAnimationTime(PULSE);
			} else {
				float percent = (float) (200 - elapsed) / 200f;
				cursize += _snapSize * percent; 
			}
		}
		_targetbox.setPosition(_targetX-cursize/2, _targetY-cursize/2);
		_targetbox.setSize(cursize, cursize);
	}

	@Override
	protected void paintWidget(GUI gui) {
		_validatedSinceLastUpdate = false;
		// Check to see if it's time to unset the grow state
		if (_targetbox.getAnimationState().getAnimationState(GROW)) {
			if (_targetbox.getAnimationState().getAnimationTime(GROW) > 150) {
				_targetbox.getAnimationState().setAnimationState(GROW, false);
				_targetbox.getAnimationState().resetAnimationTime(PULSE);
			}
		}
		super.paintWidget(gui);
	}

	// Specialized functions
	
	public void setTargetPosition(int x, int y) {
		_targetX = x;
		_targetY = y;
		invalidateLayout();
	}
	
	public void setTargetSize(int newSize) {
		if (newSize < 30)
			newSize = 30;
		_targetSize = newSize;
		invalidateLayout();
	}
	
	public void growToTarget() {
		_targetbox.getAnimationState().setAnimationState(GROW, true);
	}
	
	public void snapToTarget(int snapSize) {
		_snapSize = snapSize;
		_targetbox.getAnimationState().setAnimationState(SNAP, true);
	}

	public void setPulse(boolean enabled) {
		_targetbox.getAnimationState().setAnimationState(PULSE, enabled);
	}
	
	
}
