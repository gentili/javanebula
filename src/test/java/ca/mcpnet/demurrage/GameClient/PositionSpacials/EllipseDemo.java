package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import ca.mcpnet.demurrage.GameClient.PositionSpacials.EllipseSpacial;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class EllipseDemo extends SimpleApplication {

	Geometry _target;
	
	@Override
	public void simpleInitApp() {
		EllipseSpacial e = new EllipseSpacial(2f,0.8f);
        _target = new Geometry("ellipse", e);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        _target.setMaterial(mat);
        rootNode.attachChild(_target);
	}

	/* This is the update loop */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate
        _target.rotate(0, 0, 2*tpf);
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EllipseDemo app = new EllipseDemo();
		app.start();
	}

}
