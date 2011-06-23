package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import java.nio.FloatBuffer;

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
	  int _points;
	  
	  // Has a velo, path, accel
	  Geometry _path;
	  Arrow _posVect;

	  EllipseSpacial(float semiMajorAxis, float e, float trueAnom, AssetManager assetManager) {
		_semiMajorAxis = semiMajorAxis;
		_e = e;
		
		// Build the path
		_points = 40;
		Mesh orbitMesh = new Mesh();
		short[] indices = new short[_points];
		float[] vertexes = new float[_points*3];
		float[] colors = new float[_points*4];
		int i = 0;
		int colori = 0;
		// Last point has to be at theta
		float eccentricAnom = this.getEccentricFromTrue(trueAnom);
		for (int curpoint = 0; curpoint < _points; curpoint++) {
			float E = eccentricAnom - (float) (curpoint/(float)_points*Math.PI/4);
		    Vector3f vector3f = getPointAtEccentricAnomaly(E);
		    vertexes[i++] = vector3f.getX();
		    vertexes[i++] = vector3f.getY();
		    vertexes[i++] = vector3f.getZ();
		    indices[curpoint] = (short) curpoint;
		    
		    colors[colori++] = 0.0f; // RED
		    colors[colori++] = 1.1f - (float) curpoint / (float) (_points);   // GREEN
		    colors[colori++] = 0.0f; // BLUE
		    colors[colori++] = 1f;   // ALPHA
		}
		/* 
		// This is for the whole ellipse, we just want a chunk of the path
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
		    colors[colori++] = 0.1f + (float) curpoint / (float) (points);   // GREEN
		    colors[colori++] = 0.0f; // BLUE
		    colors[colori++] = 1f;   // ALPHA
		}
		*/
		
		orbitMesh.setMode(Mesh.Mode.LineStrip);
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
	  
	  private float getTrueFromEccentric(float E) {
		  return (float) (2*FastMath.atan2(FastMath.sqrt(1 + _e)*FastMath.sin(E/2),
				  FastMath.sqrt(1 - _e)*FastMath.cos(E/2)));	  
	  }
	  
	  private float getEccentricFromTrue(float theta) {
		  return (float) (FastMath.atan2(FastMath.sqrt(1-_e*_e)*FastMath.sin(theta),_e+FastMath.cos(theta)));
	  }
	  	  
	  private Vector3f getPointAtEccentricAnomaly(float E) {
		  return getPointAtTrueAnomaly(getTrueFromEccentric(E));
	  }

	  private Vector3f getPointAtTrueAnomaly(float theta) {
		  float r = (float) (_semiMajorAxis*(1-_e*_e)/(1+_e*FastMath.cos(theta)));
		  return new Vector3f((float)(r*FastMath.cos(theta)),(float)(r*FastMath.sin(theta)),(float) 0.0);
	  }
	  
	  public void setEccentricAnomaly(float E) {
		_posVect.setArrowExtent(getPointAtEccentricAnomaly(E));
		_posVect.getBuffer(Type.Position).setUpdateNeeded();
		// Adjust the path
		FloatBuffer buffer = _path.getMesh().getFloatBuffer(Type.Position);
        buffer.rewind();
		for (int curpoint = 0; curpoint < _points; curpoint++) {
			float curE = E - (float) (curpoint/(float)_points*Math.PI/2);
		    Vector3f vector3f = getPointAtEccentricAnomaly(curE);
		    buffer.put(vector3f.getX());
		    buffer.put(vector3f.getY());
		    buffer.put(vector3f.getZ());
		}
		_path.getMesh().getBuffer(Type.Position).setUpdateNeeded();
        _path.getMesh().updateBound();
        


	  }
}
