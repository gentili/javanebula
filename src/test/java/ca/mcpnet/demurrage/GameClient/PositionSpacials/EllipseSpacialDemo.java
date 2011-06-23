package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameClient.SimpleDemo;
import ca.mcpnet.demurrage.GameClient.PositionSpacials.EllipseSpacial;

public class EllipseSpacialDemo extends SimpleDemo {

	EllipseSpacial _e;
	EllipseSpacial _e2;
	float _curE;

	EllipseSpacialDemo() {
		super();
	}

	@Override
	public void simpleInitApp() {
		chaseCam.setDefaultDistance(5.8f);
		chaseCam.setDefaultHorizontalRotation(-1.3f);
		chaseCam.setDefaultVerticalRotation(0);
		_curE = 0;
		_e = new EllipseSpacial(2f, 0.8f, _curE, assetManager);
		_e2 = new EllipseSpacial(0.5f, 0.8f, _curE, assetManager);
		_e.attachChildToPosNode(_e2);
		rootNode.attachChild(_e);
		chaseCam.setSpatial(_e2.getPosNode());
	}

	/* This is the update loop */
	@Override
	public void simpleUpdate(float tpf) {
		_curE += tpf;
		_e.setTrueAnomaly(_e.getTrueFromEccentric(_curE/2));
		_e2.setTrueAnomaly(_e.getTrueFromEccentric(_curE*2));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EllipseSpacialDemo app = new EllipseSpacialDemo();
		app.setShowSettings(false);
		app.start();
	}

}
