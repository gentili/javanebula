package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameClient.SimpleDemo;

public class PositionTreeSpacialDemo extends SimpleDemo {

	float _curE;

	PositionTreeSpacialDemo() {
		super();
	}

	@Override
	public void simpleInitApp() {
		chaseCam.setDefaultDistance(5.8f);
		chaseCam.setDefaultHorizontalRotation(-1.3f);
		chaseCam.setDefaultVerticalRotation(0);
		_curE = 0;
		chaseCam.setSpatial(rootNode);
	}

	/* This is the update loop */
	@Override
	public void simpleUpdate(float tpf) {
		_curE += tpf;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PositionTreeSpacialDemo app = new PositionTreeSpacialDemo();
		app.setShowSettings(false);
		app.start();
	}

}
