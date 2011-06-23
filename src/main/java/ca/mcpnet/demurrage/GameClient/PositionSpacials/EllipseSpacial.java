package ca.mcpnet.demurrage.GameClient.PositionSpacials;

import java.util.Iterator;

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

/*
 * This ties the ellipse to a display method
 */
public class EllipseSpacial {
	  float _semiMajorAxis;
	  float _e; // Eccentricity
	  
	  Geometry _orbit;

	  EllipseSpacial(float semiMajorAxis, float e, AssetManager assetManager) {
		_semiMajorAxis = semiMajorAxis;
		_e = e;
		
		int points = 40;
		// Now we build the mesh
		Mesh orbitMesh = new Mesh();
		short[] indices = new short[points];
		float[] vertexes = new float[points*3];
		float[] colors = new float[points*4];
		int i = 0;
		int colori = 0;
		for (int curpoint = 0;curpoint < points;curpoint++) {
			float theta = (float) (curpoint/(float)points*2*Math.PI);
		    Vector3f vector3f = getPointAtTheta(theta);
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
		
		_orbit = new Geometry("ellipse", orbitMesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/VertexColor.j3md");
		_orbit.setMaterial(mat);
	  }
	  
	  Spatial getRootNode() {
		  return _orbit;
	  }
	  Vector3f getPointAtTheta(float E) {
		  // Radius
		  double theta = 2*FastMath.atan2(FastMath.sqrt(1 + _e)*FastMath.sin(E/2),
				  FastMath.sqrt(1 - _e)*FastMath.cos(E/2));
		  float r = (float) (_semiMajorAxis*(1-_e*_e)/(1+_e*FastMath.cos(theta)));
		  return new Vector3f((float)(r*FastMath.cos(theta)),(float)(r*FastMath.sin(theta)),(float) 0.0);

	  }

}
