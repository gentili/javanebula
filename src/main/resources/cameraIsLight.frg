#version 120

varying vec3 i_normal;
varying vec3 vertex_to_light_vector;
varying vec4 final_color;

void main() 
{
	float coef = clamp(dot(i_normal, vertex_to_light_vector), 0.0, 1.0);
	gl_FragColor = final_color * coef;
}