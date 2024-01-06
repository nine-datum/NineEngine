package nine.math;

import nine.buffer.Buffer;
import nine.function.ActionSingle;

public class Matrix4f
{
    /*
        elements layout :

        [ 0  4  8  12 ] -- line 0
        [ 1  5  9  13 ] -- line 1
        [ 2  6  10 14 ] -- line 2
        [ 3  7  11 15 ] -- line 3

        [ 0  1  2  3  ] -- column 0
        [ 4  5  6  7  ] -- column 1
        [ 8  9  10 11 ] -- column 2
        [ 12 13 14 15 ] -- column 2
    */
    private final float[] elements;

    private static float[] newElements()
    {
        float[] elements = new float[16];
        elements[0] = elements[5] = elements[10] = elements[15] = 1f;
        return elements;
    }

    private Matrix4f()
    {
        elements = newElements();
    }
    private Matrix4f(float[] elements)
    {
        this.elements = elements;
    }

    public float at(int index)
    {
        return elements[index];
    }

    public static final Matrix4f identity = new Matrix4f();

    public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        return translation(position).mul(rotation(rotation)).mul(scale(scale));
    }
    public static Matrix4f transform(Vector3f position, Vector3f rotation)
    {
        return translation(position).mul(rotation(rotation));
    }
    public static Matrix4f rotation(Vector3f rotation)
    {
        return rotationX(rotation.x)
            .mul(rotationY(rotation.y))
            .mul(rotationZ(rotation.z));
    }
    public static Matrix4f scale(Vector3f scale)
    {
        float[] elements = newElements();
        elements[0] = scale.x;
        elements[5] = scale.y;
        elements[10] = scale.z;
        return new Matrix4f(elements);
    }
    public static Matrix4f translation(Vector3f translation)
    {
        var elements = newElements();
        elements[12] = translation.x;
        elements[13] = translation.y;
        elements[14] = translation.z;
        return new Matrix4f(elements);
    }
    public static Matrix4f rotationX(float angle)
    {
        var sin = (float)Math.sin(-angle);
        var cos = (float)Math.cos(-angle);
        var elements = newElements();
        elements[5] = cos;
        elements[6] = sin;
        elements[9] = -sin;
        elements[10] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f rotationY(float angle)
    {
        var sin = (float)Math.sin(angle);
        var cos = (float)Math.cos(angle);
        var elements = newElements();
        elements[0] = cos;
        elements[2] = -sin;
        elements[8] = sin;
        elements[10] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f rotationZ(float angle)
    {
        var sin = (float)Math.sin(angle);
        var cos = (float)Math.cos(angle);
        var elements = newElements();
        elements[0] = cos;
        elements[1] = sin;
        elements[4] = -sin;
        elements[5] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f orbitalCamera(Vector3f position, Vector3f rotation, float distance)
    {
        return Matrix4f.translation(Vector3f.newZ(distance))
            .mul(Matrix4f.rotation(rotation.negative()))
            .mul(Matrix4f.translation(position.negative()));
    }
    public static Matrix4f perspective(float aspect, float fov, float near, float far)
    {
        var elements = newElements();
        float tan = (float)Math.tan(fov * 0.5f);
        elements[0] = 1f / (aspect * tan);
        elements[5] = 1f / tan;
        elements[10] = (far + near) / (far - near);
        elements[11] = 1f;
        elements[14] = -2f * far * near / (far - near);
        elements[15] = 0;
        return new Matrix4f(elements);
    }

    public Matrix4f mul(Matrix4f m)
    {
        var elements = newElements();

        for(int index = 0; index < 16; index++)
        {
            int i = index & 3;
            int j = index >> 2;
            int startA = i;
            int startB = j << 2;
            var ea = this.elements;
            var eb = m.elements;

            elements[index] = ea[startA] * eb[startB] +
                    ea[startA + 4] * eb[startB + 1] +
                    ea[startA + 8] * eb[startB + 2] +
                    ea[startA + 12] * eb[startB + 3];
        }
        return new Matrix4f(elements);
    }
    public Vector3f transformPoint(Vector3f point)
    {
        float x = point.x;
        float y = point.y;
        float z = point.z;
        float rx = elements[0] * x + elements[4] * y + elements[8] * z + elements[12];
        float ry = elements[1] * x + elements[5] * y + elements[9] * z + elements[13];
        float rz = elements[2] * x + elements[6] * y + elements[10] * z + elements[14];
        return Vector3f.newXYZ(rx, ry, rz);
    }
    public Vector3f transformVector(Vector3f vector)
    {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;
        float rx = elements[0] * x + elements[4] * y + elements[8] * z;
        float ry = elements[1] * x + elements[5] * y + elements[9] * z;
        float rz = elements[2] * x + elements[6] * y + elements[10] * z;
        return Vector3f.newXYZ(rx, ry, rz);
    }
    public Matrix4f transponed()
    {
        var t = new float[16];
        for(int i = 0; i < 16; i++)
        {
            t[i] = elements[(i >> 2) + ((i & 3) << 2)];
        }
        return new Matrix4f(t);
    }

    static float at(float[] elements, int i, int j)
    {
        return elements[i + j * 4];
    }

    public float det()
    {
        float subFactor00 = at(elements, 2, 2) * at(elements, 3, 3) - at(elements, 3, 2) * at(elements, 2, 3);
		float subFactor01 = at(elements, 2, 1) * at(elements, 3, 3) - at(elements, 3, 1) * at(elements, 2, 3);
		float subFactor02 = at(elements, 2, 1) * at(elements, 3, 2) - at(elements, 3, 1) * at(elements, 2, 2);
		float subFactor03 = at(elements, 2, 0) * at(elements, 3, 3) - at(elements, 3, 0) * at(elements, 2, 3);
		float subFactor04 = at(elements, 2, 0) * at(elements, 3, 2) - at(elements, 3, 0) * at(elements, 2, 2);
		float subFactor05 = at(elements, 2, 0) * at(elements, 3, 1) - at(elements, 3, 0) * at(elements, 2, 1);

		float dcof0 = (at(elements, 1, 1) * subFactor00 - at(elements, 1, 2) * subFactor01 + at(elements, 1, 3) * subFactor02);
		float dcof1 = -(at(elements, 1, 0) * subFactor00 - at(elements, 1, 2) * subFactor03 + at(elements, 1, 3) * subFactor04);
		float dcof2 = (at(elements, 1, 0) * subFactor01 - at(elements, 1, 1) * subFactor03 + at(elements, 1, 3) * subFactor05);
		float dcof3 = -(at(elements, 1, 0) * subFactor02 - at(elements, 1, 1) * subFactor04 + at(elements, 1, 2) * subFactor05);

		return
			at(elements, 0, 0) * dcof0 + at(elements, 0, 1) * dcof1 +
			at(elements, 0, 2) * dcof2 + at(elements, 0, 3) * dcof3;
    }

    private static float step(float[] s, int i, int j, int si, int sj)
    {
        return s[((i + si) & 3) + (((j + sj) & 3) << 2)];
    }

    public Matrix4f inversed()
    {
        var result = new float[16];
        var det = det();
        for(int index = 0; index < 16; index++)
        {
            int i = index >> 2;
            int j = index & 3;
            float subdet = Matrix3f.det(m -> step(elements, (m % 3) + i, (m / 3) + j, 1, 1));
            result[i] = subdet / det;
        }
        return new Matrix4f(result);
    }
    public Matrix4f lerp(Matrix4f b, float t)
    {
        var lerped = new float[16];
        for(int i = 0; i < 16; i++)
        {
            var ea = elements[i];
            var eb = b.elements[i];
            lerped[i] = ea + (eb - ea) * t;
        }
        return new Matrix4f(lerped);
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < 16; i++)
        {
            if(i != 0) sb.append(", ");
            sb.append(String.valueOf(elements[i]));
        } 
        sb.append("]");
        return sb.toString();
    }
    public static Matrix4f fromBuffer(Buffer<Float> buffer, int start)
    {
        float[] elements = new float[16];
        for(int i = 0; i < 16; i++) elements[i] = buffer.at(i + start);
        return new Matrix4f(elements).transponed().apply(e ->
        {
            /*var s = e.clone();
            e[1] = s[2];
            e[5] = s[6];
            e[9] = s[10];
            e[13] = s[14];

            e[2] = s[1];
            e[6] = s[5];
            e[10] = s[9];
            e[14] = s[13];*/
        });
    }
    public static Matrix4f fromArray(float[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        return new Matrix4f(elements);
    }
    public static Matrix4f fromArray(Float[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        float[] newElements = new float[16];
        for(int i = 0; i < 16; i++) newElements[i] = elements[i];
        return new Matrix4f(newElements);
    }
    public Matrix4f apply(ActionSingle<float[]> function)
    {
        float[] elements = this.elements.clone();
        function.call(elements);
        return new Matrix4f(elements);
    }
}