package ca.mcpnet.demurrage.GameClient.GL;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.Vector4f;

public class Nebula {
	private static final float MAX_SPHERE = 1f;
	private static final float MIN_SPHERE = 0.2f;
	private static final float CONTAINER_RADIUS = 2.5f;
	private static final float MAX_COMPONENT = 1f;
	
	private WireSphere _wiresphere;
	private ArrayList<GlowSphere> _gsarray;
	private ArrayList<DoublePointStar> _dpsarray;
	private boolean _complete;
	private Vector4f _finalColor;

	public Nebula() {
		_wiresphere = new WireSphere();
		_gsarray = new ArrayList<GlowSphere>();
		_dpsarray = new ArrayList<DoublePointStar>();
		_complete = false;
		_finalColor = new Vector4f();
		
		_wiresphere.setColor(0f, 1.0f, 0f, 1f);
		_wiresphere.setTranslation(Vector3f.ZERO);
		_wiresphere.setScale(CONTAINER_RADIUS);
		
		GlowSphere gs = new GlowSphere(MAX_SPHERE);
		gs.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(CONTAINER_RADIUS));
		setColor(gs);
		_gsarray.add(gs);
		
		DoublePointStar dps = new DoublePointStar(0.15f);
		dps.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(CONTAINER_RADIUS));
		setColor(dps);
		_dpsarray.add(dps);
		
		gs = new GlowSphere(MAX_SPHERE);
		gs.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(-CONTAINER_RADIUS));
		setColor(gs);
		_gsarray.add(gs);
		_finalColor.set(gs.getColor());
		
		dps = new DoublePointStar(0.15f);
		dps.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(-CONTAINER_RADIUS));
		setColor(dps);
		_dpsarray.add(dps);
	}
	
	public void draw() {
		grow();
		// _wiresphere.draw();
		glEnable(GL11.GL_BLEND);
		glDisable(GL_DEPTH_TEST);

		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		glDisable(GL11.GL_DITHER);
		for (DoublePointStar dps : _dpsarray) {
			dps.draw();
		}
		// GL14.glBlendEquation(GL14.GL_MAX);
		// glEnable(GL11.GL_DITHER);
		for (GlowSphere gs : _gsarray) {
			gs.draw();
		}
	}
	
	private void setColor(GlowSphere gs) {
		Vector3f v = gs.getTranslation();
		float red = (CONTAINER_RADIUS + v.y)/(2*CONTAINER_RADIUS);
		float mag = (float) Math.random() * 0.1f;
		gs.setColor(red*mag, 0f, mag, 1f);
	}

	private void setColor(DoublePointStar dps) {
		Vector3f v = dps.getTranslation();
		float red = (CONTAINER_RADIUS + v.y)/(2*CONTAINER_RADIUS)*MAX_COMPONENT;
		float mag = 0.1f;
		dps.setColor(red*mag, 0f, MAX_COMPONENT*mag, 1f);		
	}

	private void grow() {
		
		if (_complete)
			return;
		GlowSphere gs = _gsarray.get(_gsarray.size()-1);
		Vector4f curColor = gs.getColor();
		curColor.interpolate(_finalColor, 0.05f);
		gs.setColor(curColor.x, curColor.y, curColor.z, curColor.w);
		if (curColor.distance(_finalColor) > 0.01)
			return;
		System.out.println("New cloud");
		// Time to create a new cloud
		while (true) {
			// Direction
			Vector3f dir = new Vector3f((float)(Math.random()-0.5),(float)(Math.random()-0.5),(float)(Math.random()-0.5));
			dir.normalizeLocal();
			// Magnitude
			float mag = (float) ((Math.random()-0.5) + CONTAINER_RADIUS);
			dir.multLocal(mag);
			// Reject points already inside a nebula
			if (isClose(dir))
				continue;
			Vector3f randir = new Vector3f();
			while(true) {
				randir.set((float)(Math.random()-0.5),(float)(Math.random()-0.5),(float)(Math.random()-0.5));
				randir.normalizeLocal();
				randir.mult(0.1f);
				dir.addLocal(randir);
				if ((dir.length() > CONTAINER_RADIUS + 0.5) ||
						(dir.length() < CONTAINER_RADIUS - 0.5))
					break;
				if (isClose(dir)) {
					gs = new GlowSphere((float) (Math.random()*1f/2f+1f/4f));
					gs.setTranslation(dir);
					setColor(gs);
					_finalColor.set(gs.getColor());
					gs.setColor(0, 0, 0, 1f);
					_gsarray.add(gs);
					return;
				}
			}
		}
	}

	private boolean isClose(Vector3f dir) {
		for (GlowSphere gs : _gsarray) {
			if (dir.distance(gs.getTranslation()) < gs.getFloatScale()/2) {
				return true;
			}
		}
		return false;
	}
}
