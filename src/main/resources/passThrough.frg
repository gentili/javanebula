#version 120
varying vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
void main() {
    gl_FragColor = texture2D(texFramebuffer, pass_texcoord);
}