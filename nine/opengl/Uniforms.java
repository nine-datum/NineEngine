package nine.opengl;

import java.util.Arrays;
import java.util.stream.Collectors;

import nine.buffer.Buffer;
import nine.drawing.Color;
import nine.math.Matrix4f;
import nine.math.Vector3f;

public interface Uniforms
{
    Uniform<Matrix4f> uniformMatrix(String name);
    Uniform<Buffer<Matrix4f>> uniformMatrixArray(String name, int capacity);
    Uniform<Vector3f> uniformVector(String name);
    Uniform<Color> uniformColor(String name);
    
    static Uniforms many(Uniforms... uniforms) {
    	return new Uniforms() {
			@Override
			public Uniform<Matrix4f> uniformMatrix(String name) {
				return Uniform.many(Arrays.stream(uniforms).map(u -> u.uniformMatrix(name)).collect(Collectors.toList()));
			}

			@Override
			public Uniform<Buffer<Matrix4f>> uniformMatrixArray(String name, int capacity) {
				return Uniform.many(Arrays.stream(uniforms).map(u -> u.uniformMatrixArray(name, capacity)).collect(Collectors.toList()));
			}

			@Override
			public Uniform<Vector3f> uniformVector(String name) {
				return Uniform.many(Arrays.stream(uniforms).map(u -> u.uniformVector(name)).collect(Collectors.toList()));
			}

			@Override
			public Uniform<Color> uniformColor(String name) {
				return Uniform.many(Arrays.stream(uniforms).map(u -> u.uniformColor(name)).collect(Collectors.toList()));
			}
		};
    }
}