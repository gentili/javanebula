package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.Widget;

public abstract class ClientState {

	Widget _rootPane;
	final GameClient _gameClient;
	
	ClientState(GameClient gc) {
		_gameClient = gc;
	}
	
	abstract public void onEnterState();

	abstract public void onExitState();

	abstract public void update();

}
