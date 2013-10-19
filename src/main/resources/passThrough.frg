#version 120
varying vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
void main() {
	vec4 pixel = texture2D(texFramebuffer, pass_texcoord);
	pixel = floor(pixel*16)/16;
	float mag = 1 - (mod(gl_FragCoord.y,4)+1)/10;
	gl_FragColor = pixel*mag;
}