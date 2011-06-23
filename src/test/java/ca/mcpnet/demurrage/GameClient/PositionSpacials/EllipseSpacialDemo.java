package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameClient.SimpleDemo;
import ca.mcpnet.demurrage.GameClient.PositionSpacials.EllipseSpacial;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class EllipseSpacialDemo extends SimpleDemo {

	EllipseSpacial _e;
	float _curE;
	
	EllipseSpacialDemo() {
		super();
	}
	
	@Override
	public void simpleInitApp() {
		  _curE = 3;
		  _e = new EllipseSpacial(2f,0.8f,_curE,assetManager);
		  rootNode.attachChild(_e);
	}

	/* This is the update loop */
    @Override
    public void simpleUpdate(float tpf) {
    	_curE += tpf;
    	_e.setEccentricAnomaly(_curE);
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
