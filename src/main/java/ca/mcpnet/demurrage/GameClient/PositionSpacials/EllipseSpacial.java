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
	  AssetManager _assetManager;
	  
	  int _pathPointCount;
	  Mesh _pathMesh;
	  Geometry _pathGeom;
	  int _ellipsePointCount;
	  Mesh _ellipseMesh;
	  Geometry _ellipseGeom;
	  Arrow _posMesh;
	  Geometry _posGeom;

	  EllipseSpacial(float semiMajorAxis, float e, float trueAnom, AssetManager assetManager) {
		_semiMajorAxis = semiMajorAxis;
		_e = e;
		_assetManager = assetManager;
		
		ellipseInit();
		// Initialize the path
		pathInit(trueAnom);
		// Build the position vector
		_posMesh = new Arrow(getPointAtTrueAnomaly(trueAnom));
        _posGeom = new Geometry("PosVect", _posMesh);
        _posGeom.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md"));
        _posGeom.getMaterial().setColor("Color", ColorRGBA.Green);
        attachChild(_posGeom);
	  }
	  
	  private void ellipseInit() {
		// Initialize the ellipse
		_ellipsePointCount = 40;
		_ellipseMesh = new Mesh();
		short[] indices = new short[_ellipsePointCount];
		float[] vertexes = new float[_ellipsePointCount*3];
		int i = 0;

		for (int curpoint = 0;curpoint < _ellipsePointCount;curpoint++) {
			float theta = (float) (curpoint/(float)_ellipsePointCount*2*Math.PI);
		    Vector3f vector3f = getPointAtEccentricAnomaly(theta);
		    vertexes[i] = vector3f.getX();
		    i++;
		    vertexes[i] = vector3f.getY();
		    i++;
		    vertexes[i] = vector3f.getZ();
		    i++;
		    indices[curpoint] = (short) curpoint;
		}
		_ellipseMesh.setMode(Mesh.Mode.Points);
		_ellipseMesh.setBuffer(VertexBuffer.Type.Position, 3, vertexes);
		_ellipseMesh.setBuffer(VertexBuffer.Type.Index, 1, indices);
		_ellipseMesh.updateBound();
		_ellipseMesh.updateCounts();
		
		_ellipseGeom = new Geometry("ellipse", _ellipseMesh);
		_ellipseGeom.setMaterial(new Material(_assetManager, "Common/MatDefs/Misc/SolidColor.j3md"));
		_ellipseGeom.getMaterial().setColor("Color", ColorRGBA.Blue);
		attachChild(_ellipseGeom);
		}

	  private void pathInit(float trueAnom) {
			_pathPointCount = 40;
			_pathMesh = new Mesh();
			short[] indices = new short[_pathPointCount];
			float[] vertexes = new float[_pathPointCount*3];
			float[] colors = new float[_pathPointCount*4];
			int i = 0;
			int colori = 0;
			float eccentricAnom = this.getEccentricFromTrue(trueAnom);
			for (int curpoint = 0; curpoint < _pathPointCount; curpoint++) {
				float E = eccentricAnom - (float) (curpoint/(float)_pathPointCount*Math.PI/2);
			    Vector3f vector3f = getPointAtEccentricAnomaly(E);
			    vertexes[i++] = vector3f.getX();
			    vertexes[i++] = vector3f.getY();
			    vertexes[i++] = vector3f.getZ();
			    indices[curpoint] = (short) curpoint;
			    
			    colors[colori++] = 0.0f; // RED
			    colors[colori++] = 1.1f - (float) curpoint / (float) (_pathPointCount);   // GREEN
			    colors[colori++] = 0.0f; // BLUE
			    colors[colori++] = 1f;   // ALPHA
			}
			
			_pathMesh.setMode(Mesh.Mode.LineStrip);
			_pathMesh.setBuffer(VertexBuffer.Type.Position, 3, vertexes);
			_pathMesh.setBuffer(VertexBuffer.Type.Index, 1, indices);
			_pathMesh.setBuffer(VertexBuffer.Type.Color, 4, colors);
			_pathMesh.updateBound();
			_pathMesh.updateCounts();
			
			_pathGeom = new Geometry("ellipsePath", _pathMesh);
			_pathGeom.setMaterial(new Material(_assetManager, "Common/MatDefs/Misc/VertexColor.j3md"));
			attachChild(_pathGeom);

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
	  
	  public void setTrueAnomaly(float theta) {
		// Adjust the position pointer
		_posMesh.setArrowExtent(getPointAtTrueAnomaly(theta));
		_posMesh.getBuffer(Type.Position).setUpdateNeeded();
		// Adjust the path
		FloatBuffer buffer = _pathGeom.getMesh().getFloatBuffer(Type.Position);
        buffer.rewind();
		float E = this.getEccentricFromTrue(theta);
		for (int curpoint = 0; curpoint < _pathPointCount; curpoint++) {
			float curE = E - (float) (curpoint/(float)_pathPointCount*Math.PI/2);
		    Vector3f vector3f = getPointAtEccentricAnomaly(curE);
		    buffer.put(vector3f.getX());
		    buffer.put(vector3f.getY());
		    buffer.put(vector3f.getZ());
		}
		_pathMesh.getBuffer(Type.Position).setUpdateNeeded();
        _pathMesh.updateBound();
	  }
}
