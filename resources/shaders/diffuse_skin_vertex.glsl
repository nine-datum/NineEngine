layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texcoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec4 joints;
layout (location = 4) in vec4 weights;

out vec2 uv;
out vec3 worldNormal;
uniform mat4 transform;
uniform mat4 projection;

void main (void)
{
	uv = texcoord;
	worldNormal = joints.xyz * 0.1 + (weights.xyz + normal) * texcoord.x * 0.0;//normalize((transform * vec4(normal, 0)).xyz);
	gl_Position = (projection * transform) * vec4(position, 1);
}