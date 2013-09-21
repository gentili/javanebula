#version 120

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 color;

attribute vec3 pos;

varying vec4 final_color;

void main()
{
	vec4 mv_Pos = viewMatrix*modelMatrix*vec4(0.0,0.0,0.0,1.0);
	mv_Pos += vec4(pos,0.0);
	gl_Position = projectionMatrix*mv_Pos;
	final_color = color;
}

