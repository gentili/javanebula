#version 120
varying vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
void main() {
	vec4 pixel = texture2D(texFramebuffer, pass_texcoord);
	pixel = floor(pixel*16)/16;
	int y = int(gl_FragCoord.y);
	if (mod(y,2) == 0) {
		gl_FragColor = pixel/2;
	} else {
    	gl_FragColor = pixel;
    }
}