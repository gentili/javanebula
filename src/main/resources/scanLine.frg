#version 120
varying vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
uniform int scan_lines;

void main() {
	vec4 pixel = texture2D(texFramebuffer, pass_texcoord);
	pixel = floor(pixel*16)/16;
	float mag;
	if (scan_lines > 1) {
		mag = 1 - (mod(gl_FragCoord.y,scan_lines))/10;
	} else {
	    mag = 1;
	}
	gl_FragColor = pixel*mag;
}