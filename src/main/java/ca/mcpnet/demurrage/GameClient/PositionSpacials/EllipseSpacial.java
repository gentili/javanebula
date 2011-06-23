package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.FastMath;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Arrow;

/*
 * This ties the ellipse to a display method
 * 
 */
public class EllipseSpacial extends Node {
	  float _semiMajorAxis;
	  float _e; // Eccentricity
	  
	  // Has a velo, path, accel
	  Geometry _path;
	  Geometry _veloVect;
	  Arrow _posVect;

	  EllipseSpacial(float semiMajorAxis, float e, float trueAnom, AssetManager assetManager) {
		_semiMajorAxis = semiMajorAxis;
		_e = e;
		
		// Build the path
		int points = 40;
		Mesh orbitMesh = new Mesh();
		short[] indices = new short[points];
		float[] vertexes = new float[points*3];
		float[] colors = new float[points*4];
		int i = 0;
		int colori = 0;
		for (int curpoint = 0;curpoint < points;curpoint++) {
			float theta = (float) (curpoint/(float)points*2*Math.PI);
		    Vector3f vector3f = getPointAtEccentricAnomaly(theta);
		    vertexes[i] = vector3f.getX();
		    i++;
		    vertexes[i] = vector3f.getY();
		    i++;
		    vertexes[i] = vector3f.getZ();
		    i++;
		    indices[curpoint] = (short) curpoint;
		    
		    colors[colori++] = 0.0f; // RED
		    colors[colori++] = (float) curpoint / (float) (points);   // GREEN
		    colors[colori++] = 0.0f; // BLUE
		    colors[colori++] = 1f;   // ALPHA
		}
		
		orbitMesh.setMode(Mesh.Mode.LineLoop);
		orbitMesh.setBuffer(VertexBuffer.Type.Position, 3, vertexes);
		orbitMesh.setBuffer(VertexBuffer.Type.Index, 1, indices);
		orbitMesh.setBuffer(VertexBuffer.Type.Color, 4, colors);
		orbitMesh.updateBound();
		orbitMesh.updateCounts();
		
		_path = new Geometry("ellipsePath", orbitMesh);
		_path.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/VertexColor.j3md"));
		attachChild(_path);
		
		// Build the position vector
		_posVect = new Arrow(getPointAtTrueAnomaly(trueAnom));
        Geometry posGeom = new Geometry("PosVect", _posVect);
        posGeom.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md"));
        posGeom.getMaterial().setColor("Color", ColorRGBA.Green);
        attachChild(posGeom);
	  }
	  
	  private Vector3f getPointAtEccentricAnomaly(float E) {
		  float theta = (float) (2*FastMath.atan2(FastMath.sqrt(1 + _e)*FastMath.sin(E/2),
				  FastMath.sqrt(1 - _e)*FastMath.cos(E/2)));
		  return getPointAtTrueAnomaly(theta);
	  }

	  private Vector3f getPointAtTrueAnomaly(float theta) {
		  float r = (float) (_semiMajorAxis*(1-_e*_e)/(1+_e*FastMath.cos(theta)));
		  return new Vector3f((float)(r*FastMath.cos(theta)),(float)(r*FastMath.sin(theta)),(float) 0.0);
	  }
	  
	  public void setEccentricAnomaly(float E) {
		_posVect.setArrowExtent(getPointAtEccentricAnomaly(E));
		_posVect.getBuffer(Type.Position).setUpdateNeeded();

	  }
}
