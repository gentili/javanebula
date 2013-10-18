#version 120
varying vec2 Texcoord;

uniform sampler2D texFramebuffer;
void main() {
    gl_FragColor = vec4(0,1,0,1);
    // texture(texFramebuffer, Texcoord);
}