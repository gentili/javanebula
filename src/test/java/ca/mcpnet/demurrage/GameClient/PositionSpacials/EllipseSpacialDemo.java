package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameClient.SimpleDemo;
import ca.mcpnet.demurrage.GameClient.PositionSpacials.EllipseSpacial;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class EllipseSpacialDemo extends SimpleDemo {

	Geometry _target;
	
	EllipseSpacialDemo() {
		super();
	}
	
	@Override
	public void simpleInitApp() {
		  EllipseSpacial e = new EllipseSpacial(2f,0.8f,assetManager);
		  rootNode.attachChild(e.getRootNode());
	}

	/* This is the update loop */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate
        // _target.rotate(0, 0, 2*tpf);
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
