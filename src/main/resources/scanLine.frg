#version 150
in vec2 pass_texcoord;

uniform sampler2D texFramebuffer;
uniform int scan_lines;

out vec4 frag_color;

void main() {
	vec4 pixel = texture(texFramebuffer, pass_texcoord);
	pixel = floor(pixel*16)/16;
	float mag;
	int mod_coord = int(mod(gl_FragCoord.y,scan_lines));
	int adj_coord = abs(mod_coord - scan_lines/2);
//	if (mod_coord == 0) {
//	    frag_color = vec4(0.0,1.0,0.0,1.0);
//	} else if (mod_coord == scan_lines -1) {
//	    frag_color = vec4(1.0,0.0,0.0,1.0);
//	} else if (adj_coord == 0) {
//	    frag_color = vec4(0.0,0.0,1.0,1.0);	
	mag = 1 - float(adj_coord)/float(scan_lines);
	frag_color = pixel*mag;
	
}