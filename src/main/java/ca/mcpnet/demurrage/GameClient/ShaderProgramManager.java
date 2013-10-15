package ca.mcpnet.demurrage.GameClient;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Vector;

import org.lwjgl.opengl.GL20;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.ConcursionEdge;
import ca.mcpnet.demurrage.GameClient.GL.ConcursionPoint;
import ca.mcpnet.demurrage.GameClient.GL.PointStar;
import ca.mcpnet.demurrage.GameClient.GL.GlowSphere;
import ca.mcpnet.demurrage.GameClient.GL.Shader;
import ca.mcpnet.demurrage.GameClient.GL.ShaderProgram3D;
import ca.mcpnet.demurrage.GameClient.GL.WireSphere;

public class ShaderProgramManager {
	private Vector<ShaderProgram3D> _shaderProgramList;

	private ShaderProgram3D _simpleIndexedColorShaderProgram;
	private ShaderProgram3D _cameraIsLightShaderProgram;
	private ShaderProgram3D _concursionPointShaderProgram;
	private ShaderProgram3D _concursionEdgeShaderProgram;
	private ShaderProgram3D _glowSphereShaderProgram;
	

	public ShaderProgramManager() throws IOException {
		_shaderProgramList = new Vector<ShaderProgram3D>();

		_simpleIndexedColorShaderProgram = new ShaderProgram3D();
		_simpleIndexedColorShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/simpleIndexedColor.vrt"));
		_simpleIndexedColorShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/simpleIndexedColor.frg"));
		_simpleIndexedColorShaderProgram.attachAndLink();
		_shaderProgramList.add(_simpleIndexedColorShaderProgram);

		_cameraIsLightShaderProgram = new ShaderProgram3D();
        _cameraIsLightShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/cameraIsLight.vrt"));
        _cameraIsLightShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/cameraIsLight.frg"));
        _cameraIsLightShaderProgram.attachAndLink();
        _shaderProgramList.add(_cameraIsLightShaderProgram);
        
        _concursionPointShaderProgram = new ShaderProgram3D();
        _concursionPointShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/concursionPoint.vrt"));
        _concursionPointShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/concursionPoint.frg"));
        _concursionPointShaderProgram.attachAndLink();
        _shaderProgramList.add(_concursionPointShaderProgram);

        _concursionEdgeShaderProgram = new ShaderProgram3D();
        _concursionEdgeShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/concursionEdge.vrt"));
        _concursionEdgeShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/concursionEdge.frg"));
        _concursionEdgeShaderProgram.attachAndLink();
        _shaderProgramList.add(_concursionEdgeShaderProgram);
        
        _glowSphereShaderProgram = new ShaderProgram3D();
        _glowSphereShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/glowSphere.vrt"));
        _glowSphereShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/glowSphere.frg"));
        _glowSphereShaderProgram.attachAndLink();
        _shaderProgramList.add(_glowSphereShaderProgram);
        
        Axis.setShaderProgramManager(this);
        ConcursionEdge.setShaderProgramManager(this);
        ConcursionPoint.setShaderProgramManager(this);
        GlowSphere.setShaderProgramManager(this);
        WireSphere.setShaderProgramManager(this);
        PointStar.setShaderProgramManager(this);
	}
	
	public ShaderProgram3D simpleIndexedColorShaderProgram() {
		return _simpleIndexedColorShaderProgram;
	}

	public ShaderProgram3D cameraIsLightShaderProgram() {
		return _cameraIsLightShaderProgram;
	}
	
	public ShaderProgram3D concursionPointShaderProgram() {
		return _concursionPointShaderProgram;
	}

	public ShaderProgram3D concursionEdgeShaderProgram() {
		return _concursionEdgeShaderProgram;
	}

	public ShaderProgram3D glowSphereShaderProgram() {
		return _glowSphereShaderProgram;
	}

	public void setShaderProgramMatrixes(
			FloatBuffer projectionMatrixFloatBuffer,
			FloatBuffer viewMatrixFloatBuffer) {
		Iterator<ShaderProgram3D> spitr = _shaderProgramList.iterator();
		while (spitr.hasNext()) {
			ShaderProgram3D sp = spitr.next();
			if (projectionMatrixFloatBuffer != null) 
				sp.setProjectionMatrix(projectionMatrixFloatBuffer);
			if (viewMatrixFloatBuffer != null)
				sp.setViewMatrix(viewMatrixFloatBuffer);
		}

	}

}