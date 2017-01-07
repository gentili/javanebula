package ca.mcpnet.javanebula;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Vector;

import org.lwjgl.opengl.GL20;

import ca.mcpnet.javanebula.GL.Axis;
import ca.mcpnet.javanebula.GL.GlowSphere;
import ca.mcpnet.javanebula.GL.PixellationFBO;
import ca.mcpnet.javanebula.GL.PointStar;
import ca.mcpnet.javanebula.GL.Shader;
import ca.mcpnet.javanebula.GL.ShaderProgram2D;
import ca.mcpnet.javanebula.GL.ShaderProgram3D;

public class ShaderProgramManager {
	private Vector<ShaderProgram3D> _shaderProgram3DList;
	private Vector<ShaderProgram2D> _shaderProgram2DList;

	private ShaderProgram3D _simpleIndexedColorShaderProgram;
	private ShaderProgram3D _cameraIsLightShaderProgram;
	private ShaderProgram3D _concursionPointShaderProgram;
	private ShaderProgram3D _concursionEdgeShaderProgram;
	private ShaderProgram3D _glowSphereShaderProgram;
	private ShaderProgram2D _passThroughShaderProgram;
	private ShaderProgram2D _scanLineShaderProgram;
	

	public ShaderProgramManager() throws IOException {
		_shaderProgram3DList = new Vector<ShaderProgram3D>();
		_shaderProgram2DList = new Vector<ShaderProgram2D>();

		_simpleIndexedColorShaderProgram = new ShaderProgram3D();
		_simpleIndexedColorShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/simpleIndexedColor.vrt"));
		_simpleIndexedColorShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/simpleIndexedColor.frg"));
		_simpleIndexedColorShaderProgram.attachAndLink();
		_shaderProgram3DList.add(_simpleIndexedColorShaderProgram);

		_cameraIsLightShaderProgram = new ShaderProgram3D();
        _cameraIsLightShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/cameraIsLight.vrt"));
        _cameraIsLightShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/cameraIsLight.frg"));
        _cameraIsLightShaderProgram.attachAndLink();
        _shaderProgram3DList.add(_cameraIsLightShaderProgram);
        
        _concursionPointShaderProgram = new ShaderProgram3D();
        _concursionPointShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/concursionPoint.vrt"));
        _concursionPointShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/concursionPoint.frg"));
        _concursionPointShaderProgram.attachAndLink();
        _shaderProgram3DList.add(_concursionPointShaderProgram);
        
        _glowSphereShaderProgram = new ShaderProgram3D();
        _glowSphereShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/glowSphere.vrt"));
        _glowSphereShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/glowSphere.frg"));
        _glowSphereShaderProgram.attachAndLink();
        _shaderProgram3DList.add(_glowSphereShaderProgram);
        
        _passThroughShaderProgram = new ShaderProgram2D();
        _passThroughShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/passThrough.vrt"));
        _passThroughShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/passThrough.frg"));
        _passThroughShaderProgram.attachAndLink();
        _shaderProgram2DList.add(_passThroughShaderProgram);
        
        _scanLineShaderProgram = new ShaderProgram2D();
        _scanLineShaderProgram.addShader(new Shader(GL20.GL_VERTEX_SHADER,"/scanLine.vrt"));
        _scanLineShaderProgram.addShader(new Shader(GL20.GL_FRAGMENT_SHADER,"/scanLine.frg"));
        _scanLineShaderProgram.attachAndLink();
        _shaderProgram2DList.add(_scanLineShaderProgram);
        
        Axis.setShaderProgramManager(this);
        GlowSphere.setShaderProgramManager(this);
        PointStar.setShaderProgramManager(this);
        PixellationFBO.setShaderProgramManager(this);
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

	public void setShaderProgram3DMatrixes(
			FloatBuffer projectionMatrixFloatBuffer,
			FloatBuffer viewMatrixFloatBuffer) {
		Iterator<ShaderProgram3D> spitr = _shaderProgram3DList.iterator();
		while (spitr.hasNext()) {
			ShaderProgram3D sp = spitr.next();
			if (projectionMatrixFloatBuffer != null) 
				sp.setProjectionMatrix(projectionMatrixFloatBuffer);
			if (viewMatrixFloatBuffer != null)
				sp.setViewMatrix(viewMatrixFloatBuffer);
		}

	}

	public ShaderProgram2D passThroughShaderProgram() {
		return _passThroughShaderProgram;
	}
	
	public ShaderProgram2D scanLineShaderProgram() {
		return _scanLineShaderProgram;
	}

}