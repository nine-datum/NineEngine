in vec3 position;
in vec2 texcoord;
in vec3 normal;

out vec2 uv;
out vec3 worldNormal;
uniform mat4 transform;

void main (void)
{
	uv = texcoord;
	worldNormal = transform * vec4(normal, 0);
	gl_Position = transform * vec4(position, 1);
}