#version 120

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

attribute vec3 pos;
attribute vec4 color;

varying vec4 final_color;

void main()
{    
	gl_Position = projectionMatrix*viewMatrix*modelMatrix*vec4(pos,1.0);
	
	final_color = color;
}

