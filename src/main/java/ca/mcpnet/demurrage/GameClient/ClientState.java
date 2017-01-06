package ca.mcpnet.demurrage.GameClient;


public abstract class ClientState {

	final GameClient _gameClient;
	
	ClientState(GameClient gc) {
		_gameClient = gc;
	}
	
	abstract public void onEnterState();

	abstract public void onExitState();

	abstract public void update();

}
