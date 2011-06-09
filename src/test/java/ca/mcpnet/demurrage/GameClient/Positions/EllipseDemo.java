package ca.mcpnet.demurrage.GameClient.Positions;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class EllipseDemo extends SimpleApplication {

	Geometry player;
	
	@Override
	public void simpleInitApp() {
		Line l = new Line(new Vector3f(1,1,1), new Vector3f(-1,-1,-1));
        player = new Geometry("blue cube", l);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
	}

	/* This is the update loop */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate
        player.rotate(0, 2*tpf, 0);
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EllipseDemo app = new EllipseDemo();
		app.start();
	}

}
