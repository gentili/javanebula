#version 120

varying vec2 orig_pos;
varying vec4 final_color;

void main() 
{
    float radius = abs(length(orig_pos)) * 1.414;
	if (radius < 1) {
		gl_FragColor = final_color * (1.0 - radius*radius);
	} else {
		gl_FragColor = vec4(0,0,0,1);
	}
}