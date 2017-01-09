#version 150
in vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
uniform int scan_lines;

out vec4 frag_color;

void main() {
	vec4 pixel = texture(texFramebuffer, pass_texcoord);
	pixel = floor(pixel*16)/16;
	float mag;
	if (scan_lines > 1) {
		mag = 1 - (mod(gl_FragCoord.y,scan_lines))/10;
	} else {
	    mag = 1;
	}
	frag_color = pixel*mag;
	
}