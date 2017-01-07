package ca.mcpnet.javanebula.GL;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import ca.mcpnet.javanebula.jme.Vector3f;
import ca.mcpnet.javanebula.jme.Vector4f;

public class Nebula {
	private static final Logger _log = Logger.getLogger("Nebula");

	private static final float MAX_SPHERE = 1f;
	private static final float CONTAINER_RADIUS = 2f;
	
	private float _star_interpolation = 0.01f;
	private float _glow_interpolation = 0.1f;
	
	private ArrayList<GlowSphere> _gsarray;
	private ArrayList<DoublePointStar> _dpsarray;
	private Vector4f _finalGlowColor;
	private Vector4f _finalStarColor;

	public Nebula() {
		_gsarray = new ArrayList<GlowSphere>();
		_dpsarray = new ArrayList<DoublePointStar>();
		_finalGlowColor = new Vector4f();
		_finalStarColor = new Vector4f();
				
		GlowSphere gs = new GlowSphere(MAX_SPHERE);
		gs.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(CONTAINER_RADIUS));
		setColor(gs);
		gs.getColor().normalizeLocal();
		gs.getColor().multLocal(0.2f);
		_gsarray.add(gs);
		
		DoublePointStar dps = new DoublePointStar(0.2f);
		dps.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(CONTAINER_RADIUS));
		setColor(dps);
		_dpsarray.add(dps);
		
		gs = new GlowSphere(MAX_SPHERE);
		gs.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(-CONTAINER_RADIUS));
		setColor(gs);
		gs.getColor().normalizeLocal();
		gs.getColor().multLocal(0.2f);
		_gsarray.add(gs);
		_finalGlowColor.set(gs.getColor());
		gs.setColor(Vector4f.ZERO);
		
		dps = new DoublePointStar(0.2f);
		dps.setTranslation(Vector3f.UNIT_XYZ.normalize().mult(-CONTAINER_RADIUS));
		setColor(dps);
		_dpsarray.add(dps);
		_finalStarColor.set(dps.getColor());
		dps.setColor(Vector4f.ZERO);
	}
	
	public void draw() {
		grow();
		glEnable(GL11.GL_BLEND);
		glDisable(GL_DEPTH_TEST);

		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		glEnable(GL11.GL_DITHER);
		// glDisable(GL11.GL_DITHER);
		for (DoublePointStar dps : _dpsarray) {
			dps.draw();
		}
		/*
		DoublePointStar dps = _dpsarray.get(_dpsarray.size()-1);
		_wiresphere.setScale(0.2f);
		_wiresphere.setTranslation(dps.getTranslation());
		_wiresphere.setColor(0.2f,0f,0f,1f);
		_wiresphere.draw();
		*/
		// GL14.glBlendEquation(GL14.GL_MAX);
		for (GlowSphere gs : _gsarray) {
			gs.draw();
		}
		/*
		GlowSphere gs = _gsarray.get(_gsarray.size()-1);
		_wiresphere.setScale(gs.getFloatScale()/2);
		_wiresphere.setTranslation(gs.getTranslation());
		_wiresphere.setColor(0f,0.2f,0f,1f);
		_wiresphere.draw();
		*/
	}
	
	public void setInterpolations(float star, float glow) {
		_star_interpolation = star;
		_glow_interpolation = glow;
	}
	
	private void setColor(GlowSphere gs) {
		Vector3f v = gs.getTranslation();
		float red = (CONTAINER_RADIUS + v.y)/(2*CONTAINER_RADIUS);
		float mag = 0.05f;
		gs.setColor(red*mag, 0f, mag, 0f);
	}

	private void setColor(DoublePointStar dps) {
		Vector3f v = dps.getTranslation();
		float red = (CONTAINER_RADIUS + v.y)/(2*CONTAINER_RADIUS);
		float mag = 0.1f;
		dps.setColor(red*mag, 0f, mag, 0f);
	}

	private void grow() {
		if (_dpsarray.size() < 31) {
			DoublePointStar dps = _dpsarray.get(_dpsarray.size()-1);
			Vector4f curColor = dps.getColor();
			curColor.interpolate(_finalStarColor, _star_interpolation);
			dps.setColor(curColor.x, curColor.y, curColor.z, curColor.w);
			if (curColor.distance(_finalStarColor) < 0.01)
				newStar();
			if (_dpsarray.size() == 31)
				_log.debug("Final DoublePointStar generated");
		}
		
		if (_gsarray.size() < 400) {
			GlowSphere gs = _gsarray.get(_gsarray.size()-1);
			Vector4f curColor = gs.getColor();
			curColor.interpolate(_finalGlowColor, _glow_interpolation);
			gs.setColor(curColor.x, curColor.y, curColor.z, curColor.w);
			if (curColor.distance(_finalGlowColor) < 0.01)
				newCloud();
			if (_gsarray.size() == 400) {
				_log.debug("Final GlowSphere generated");
			}
		}
	}

	private void newStar() {
		// Direction
		Vector3f dir = new Vector3f((float)(Math.random()-0.5),(float)(Math.random()-0.5),(float)(Math.random()-0.5));
		dir.normalizeLocal();
		// Magnitude
		float mag = (float) ((Math.random()-0.5) + CONTAINER_RADIUS);
		dir.multLocal(mag);
		float size = (float) (Math.random()*0.07f+0.01f);
		DoublePointStar dps = new DoublePointStar(size);
		dps.setTranslation(dir);
		setColor(dps);
		_dpsarray.add(dps);
		_finalStarColor.set(dps.getColor());
		dps.setColor(Vector4f.ZERO);
		
		GlowSphere gs = new GlowSphere(size*10);
		gs.setTranslation(dir);
		setColor(gs);
		gs.getColor().normalizeLocal();
		gs.getColor().multLocal(0.2f);
		_gsarray.add(gs);
		_finalGlowColor.set(gs.getColor());
		gs.setColor(Vector4f.ZERO);

	}

	private void newCloud() {
		// Time to create a new cloud
		while (true) {
			// Direction
			Vector3f dir = new Vector3f((float)(Math.random()-0.5),(float)(Math.random()-0.5),(float)(Math.random()-0.5));
			dir.normalizeLocal();
			// Magnitude
			float mag = (float) ((Math.random()-0.5) + CONTAINER_RADIUS);
			dir.multLocal(mag);
			// Reject points already inside a nebula
			if (isTouching(dir))
				continue;
			Vector3f randir = new Vector3f();
			while(true) {
				randir.set((float)(Math.random()-0.5),(float)(Math.random()-0.5),(float)(Math.random()-0.5));
				randir.normalizeLocal();
				randir.mult(0.05f);
				dir.addLocal(randir);
				if ((dir.length() > CONTAINER_RADIUS + 0.5))
						// || (dir.length() < CONTAINER_RADIUS - 0.5))
					break;
				if (isTouching(dir)) {
					// Need to find the closest star
					float dist = Float.MAX_VALUE;
					for (DoublePointStar dps : _dpsarray) {
						float curdist = dir.distance(dps.getTranslation()); 
						if (curdist < dist)
							dist = curdist;
					}
					float size = 1 - dist;
					if (size < 0.05)
						break;
					GlowSphere gs = new GlowSphere(size);
					gs.setTranslation(dir);
					setColor(gs);
					_finalGlowColor.set(gs.getColor());
					gs.setColor(0, 0, 0, 1f);
					_gsarray.add(gs);
					return;
				}
			}
		}
	}


	private boolean isTouching(Vector3f dir) {
		for (GlowSphere gs : _gsarray) {
			if (dir.distance(gs.getTranslation()) < gs.getFloatScale()/1.7f) {
				return true;
			}
		}
		return false;
	}
}
