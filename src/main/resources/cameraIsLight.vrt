#version 120

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 color;

attribute vec3 pos;
attribute vec3 normal;

varying vec3 i_normal;
varying vec3 vertex_to_light_vector;
varying vec4 final_color;

void main()
{    
	vec4 mv_Pos = viewMatrix*modelMatrix*vec4(pos,1.0);
	gl_Position = projectionMatrix*viewMatrix*modelMatrix*vec4(pos,1.0);
	
	i_normal = normalize((viewMatrix * modelMatrix * vec4(normal,0)).xyz);
	vec3 light_Pos = vec3(-0.0,0.0,0.0);
	vertex_to_light_vector = normalize(light_Pos-mv_Pos.xyz);
	final_color = color;
}

