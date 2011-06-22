package ca.mcpnet.demurrage.GameClient;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class CameraDemo extends SimpleDemo {
    
	@Override
	public void simpleInitApp() {
		Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
        
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CameraDemo app = new CameraDemo();
		app.setShowSettings(false);
		app.start();
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}

}
