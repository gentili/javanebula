#version 150

in vec4 final_color;
out vec4 frag_color;

void main() 
{
	frag_color = final_color;
}