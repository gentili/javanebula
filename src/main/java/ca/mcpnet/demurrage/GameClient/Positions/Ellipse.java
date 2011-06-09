package ca.mcpnet.demurrage.GameClient.Positions;

import java.util.Iterator;

import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.FastMath;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;

/*
 * This ties the ellipse to a display method
 */
public class Ellipse extends Mesh {
	  float _semiMajorAxis;
	  float _e; // Eccentricity

	  Ellipse(float semiMajorAxis, float e) {
		  _semiMajorAxis = semiMajorAxis;
		  _e = e;
		  /*
		  this.setMode(Mesh.Mode.LineLoop);
	      setBuffer(Type.Position, 3, new float[]{
	    		  0f,    0f,    0f,
                  _semiMajorAxis, 0f, 0f,
                  _semiMajorAxis, _semiMajorAxis, 0f,
                  0f, _semiMajorAxis, 0f
                  });
	      setBuffer(Type.Index, 1, new short[]{0,1,2,3});
	      updateBound();
	      updateCounts();
	      */

		  int points = 40;
		  // Now we build the mesh
	        float[] array = new float[points*3];
	        short[] indices = new short[points];
	        int i = 0;
	        for (int curpoint = 0;curpoint < points;curpoint++) {
	        	float theta = (float) (curpoint/(float)points*2*Math.PI);
	            Vector3f vector3f = getPointAtTheta(theta);
	            array[i] = vector3f.getX();
	            i++;
	            array[i] = vector3f.getY();
	            i++;
	            array[i] = vector3f.getZ();
	            i++;
                indices[curpoint] = (short) curpoint;
                System.out.println(theta+" "+vector3f.getX()+" "+vector3f.getY()+" "+vector3f.getZ());
	        }

	        this.setMode(Mesh.Mode.LineLoop);
	        this.setBuffer(VertexBuffer.Type.Position, 3, array);
	        this.setBuffer(VertexBuffer.Type.Index, 1, indices);
	        this.updateBound();
	        this.updateCounts();
	  }
	  Vector3f getPointAtTheta(float E) {
		  // Radius
		  double theta = 2*FastMath.atan2(FastMath.sqrt(1 + _e)*FastMath.sin(E/2),
				  FastMath.sqrt(1 - _e)*FastMath.cos(E/2));
		  float r = (float) (_semiMajorAxis*(1-_e*_e)/(1+_e*FastMath.cos(theta)));
		  return new Vector3f((float)(r*FastMath.cos(theta)),(float)(r*FastMath.sin(theta)),(float) 0.0);

	  }

}
