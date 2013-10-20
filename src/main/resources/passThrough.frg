#version 120
varying vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
void main() {
	vec4 pixel = texture2D(texFramebuffer, pass_texcoord);
	gl_FragColor = pixel;
}