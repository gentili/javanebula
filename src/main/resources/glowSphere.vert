#version 120

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 color;

attribute vec3 pos;

varying vec2 orig_pos;
varying vec4 final_color;

void main()
{
	orig_pos = normalize(vec2(pos));
	vec4 mv_pos = viewMatrix*modelMatrix*vec4(0.0,0.0,0.0,1.0);
	mv_pos += vec4(pos,0.0);
	gl_Position = projectionMatrix*mv_pos;
	
	// Pass along the color
	final_color = color;
}

