package ca.mcpnet.demurrage.GameClient.GL;

import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameClient.jme.Vector4f;

public class DoublePointStar {
	
	private PointStar _outterPointStar;
	private PointStar _innerPointStar;

	public DoublePointStar(float scale) {
		_outterPointStar = new PointStar(scale);
		_innerPointStar = new PointStar(scale/2.5f);
	}
	
	public void setColor(Vector4f color) {
		_outterPointStar.setColor(color);
		_innerPointStar.setColor(color.add(0.1f, 0.1f, 0.1f, 0f).normalizeLocal().mult(color.length()));
	}
	public void setColor(float f, float g, float h, float i) {
		setColor(new Vector4f(f,g,h,i));
	}
	public void setTranslation(Vector3f trans) {
		_outterPointStar.setTranslation(trans);
		_innerPointStar.setTranslation(trans);
	}
	public void draw() {
		_outterPointStar.draw();
		_innerPointStar.draw();
	}

	public Vector3f getTranslation() {
		return _outterPointStar.getTranslation();
	}

	public Vector4f getColor() {
		return _outterPointStar.getColor();
	}


}
