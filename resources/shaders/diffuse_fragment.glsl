in vec2 uv;
in vec3 worldNormal;

out vec4 out_Color;

uniform sampler2D texture2d;
uniform vec3 worldLight;

void main (void)
{	
	out_Color = vec4(vec3(1,1,1) * (dot(worldLight, worldNormal) + 1) * 0.5, 1);
}