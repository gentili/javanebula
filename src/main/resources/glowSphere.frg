#version 150

in vec2 orig_pos;
in vec4 final_color;

out vec4 frag_color;

void main() 
{
    float radius = abs(length(orig_pos)) * 1.414;
	if (radius < 1) {
		frag_color = final_color * (1.0 - radius*radius);
	} else {
		frag_color = vec4(0,0,0,1);
	}
}