#version 120

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 color;

attribute vec3 pos;

varying vec4 final_color;

void main()
{
	gl_Position = projectionMatrix*viewMatrix*modelMatrix*vec4(pos,1.0);
	final_color = color;
}

