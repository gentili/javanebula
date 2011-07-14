package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import com.jme3.math.Quaternion;

import ca.mcpnet.demurrage.GameClient.SimpleDemo;
import ca.mcpnet.demurrage.GameClient.PositionSpacials.EllipseSpacial;

public class EllipseSpacialDemo extends SimpleDemo {

	EllipseSpacial _e;
	EllipseSpacial _e2;
	EllipseSpacial _e3;
	Quaternion _q;
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
		_q = new Quaternion();
		_e = new EllipseSpacial(2f, 0.8f, _curE, Quaternion.IDENTITY, assetManager);
		_e2 = new EllipseSpacial(0.5f, 0.1f, _curE, Quaternion.IDENTITY, assetManager);
		_e3 = new EllipseSpacial(0.2f, 0.5f, _curE, Quaternion.IDENTITY, assetManager);
		_e2.attachChildToPosNode(_e3);
		_e.attachChildToPosNode(_e2);
		rootNode.attachChild(_e);
		chaseCam.setSpatial(_e);
	}

	/* This is the update loop */
	@Override
	public void simpleUpdate(float tpf) {
		_curE += tpf/10f;
		_e.setTrueAnomaly(_curE);
		_e2.setTrueAnomaly(_e2.getTrueFromEccentric(_curE*2));
		_e3.setTrueAnomaly(_e3.getTrueFromEccentric(_curE*3));
		_q.fromAngles(0,_curE,0);
		_e3.setLocalRotation(_q);
		_q.fromAngles(0,0,_curE);
		_e2.setLocalRotation(_q);
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
