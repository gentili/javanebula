package ca.mcpnet.demurrage.GameClient;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Box;

public class CameraDemo extends SimpleDemo {

	Arrow _a;
	float _alen;
	
	@Override
	public void simpleInitApp() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
		
        _alen = 1f;
		_a = new Arrow(new Vector3f(1f,1f,1f));
		_a.setStreamed();
        Geometry ageom = new Geometry("Arrow", _a);
        ageom.setMaterial(mat);
        rootNode.attachChild(ageom);
        
        Grid g = new Grid(10, 10, 1);
        Geometry ggeom = new Geometry("Grid", g);
        ggeom.setMaterial(mat);
        rootNode.attachChild(ggeom);
        
        WireBox w = new WireBox(3,3,3);
        Geometry wgeom = new Geometry("Wirebox", w);
        wgeom.setMaterial(mat);
        rootNode.attachChild(wgeom);        

        WireSphere s = new WireSphere(1);
        Geometry sgeom = new Geometry("WireSphere", s);
        sgeom.setMaterial(mat);
        rootNode.attachChild(sgeom);        
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

	@Override
	public void simpleUpdate(float tpf) {
		_alen += tpf;
		_a.setArrowExtent(new Vector3f(_alen,_alen,_alen));
		_a.getBuffer(Type.Position).setUpdateNeeded();
	}

}
