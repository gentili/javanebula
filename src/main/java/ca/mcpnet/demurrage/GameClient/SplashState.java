package ca.mcpnet.demurrage.GameClient;

import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.utils.TintAnimator;

public class SplashState extends ClientState {

	static enum StartStateEnum {
		START,
		GORZOFADEIN,
		GORZOFADEOUT
	}
	
	StartStateEnum _startstate;
	boolean _skip;
	
	SplashState(GameClient gc) {
		super(gc);
		_rootPane = new SplashWidget();
	}

	@Override
	public void onEnterState() {
		_gameClient.getGUI().setRootPane(_rootPane);
		_skip = false;
		_startstate = StartStateEnum.START;
		if (_rootPane.getTintAnimator() == null) {
			_rootPane.setTintAnimator(new TintAnimator(_gameClient.getGUI()));
		}
		_rootPane.getTintAnimator().setColor(Color.BLACK);
	}

	@Override
	public void onExitState() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

		if (_skip) {
			_gameClient.changeState(_gameClient._mainMenuState);
		}
		if (!_rootPane.getTintAnimator().isFadeActive()) {
			if (_startstate == StartStateEnum.START) {
				_rootPane.setOverlay(_gameClient.getTheme().getImage("gorzo"));
				_rootPane.getTintAnimator().setColor(Color.BLACK);
				_rootPane.getTintAnimator().fadeTo(Color.GREEN, 2000);
				_startstate = StartStateEnum.GORZOFADEIN;
				
			} else if (_startstate == StartStateEnum.GORZOFADEIN) {
				_rootPane.getTintAnimator().fadeToHide(2000);
				_startstate = StartStateEnum.GORZOFADEOUT;
				
			} else if (_startstate == StartStateEnum.GORZOFADEOUT) {
				_gameClient.changeState(_gameClient._mainMenuState);
			}
		}
	}
	
	class SplashWidget extends Widget {

		@Override
		protected boolean handleEvent(Event evt) {
			if (evt.isKeyPressedEvent()) {
				_skip = true;
			}
			return super.handleEvent(evt);
		}

		@Override
		protected void beforeRemoveFromGUI(GUI gui) {
			System.out.println("Removing from GUI!");
			super.beforeRemoveFromGUI(gui);
		}
	}

}
