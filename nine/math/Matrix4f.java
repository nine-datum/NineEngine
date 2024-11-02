package nine.math;

import java.nio.FloatBuffer;

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
    private final double[] elements;

    private static double[] newElements()
    {
        double[] elements = new double[16];
        elements[0] = elements[5] = elements[10] = elements[15] = 1f;
        return elements;
    }

    private Matrix4f()
    {
        elements = newElements();
    }
    private Matrix4f(double[] elements)
    {
        this.elements = elements;
    }

    public double at(int index)
    {
        return elements[index];
    }

    public static final Matrix4f identity = new Matrix4f();

    private static final Matrix4f flipZY = new Matrix4f(new double[]
    {
        1, 0, 0, 0,
        0, 0, 1, 0,
        0, 1, 0, 0,
        0, 0, 0, 1,
    });

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
        return rotationZ(rotation.z)
            .mul(rotationY(rotation.y))
            .mul(rotationX(rotation.x));
    }
    public static Matrix4f scale(Vector3f scale)
    {
        double[] elements = newElements();
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
    public static Matrix4f rotationX(double angle)
    {
        var sin = Math.sin(angle);
        var cos = Math.cos(angle);
        var elements = newElements();
        elements[5] = cos;
        elements[6] = sin;
        elements[9] = -sin;
        elements[10] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f rotationY(double angle)
    {
        var sin = Math.sin(angle);
        var cos = Math.cos(angle);
        var elements = newElements();
        elements[0] = cos;
        elements[2] = -sin;
        elements[8] = sin;
        elements[10] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f rotationZ(double angle)
    {
        var sin = Math.sin(angle);
        var cos = Math.cos(angle);
        var elements = newElements();
        elements[0] = cos;
        elements[1] = sin;
        elements[4] = -sin;
        elements[5] = cos;
        return new Matrix4f(elements);
    }
    public static Matrix4f orbitalCamera(Vector3f position, Vector3f rotation, double distance)
    {
        return Matrix4f.translation(Vector3f.newZ(distance))
            .mul(Matrix4f.rotation(rotation.negative()))
            .mul(Matrix4f.translation(position.negative()));
    }
    public static Matrix4f firstPersonCamera(Vector3f position, Vector3f rotation)
    {
        return Matrix4f.rotationX(-rotation.x)
            .mul(Matrix4f.rotationY(-rotation.y))
            .mul(Matrix4f.translation(position.negative()));
    }
    public static Matrix4f perspective(double aspect, double fov, double near, double far)
    {
        var elements = newElements();
        double tan = Math.tan(fov * 0.5f);
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
        double x = point.x;
        double y = point.y;
        double z = point.z;
        double rx = elements[0] * x + elements[4] * y + elements[8] * z + elements[12];
        double ry = elements[1] * x + elements[5] * y + elements[9] * z + elements[13];
        double rz = elements[2] * x + elements[6] * y + elements[10] * z + elements[14];
        return Vector3f.newXYZ(rx, ry, rz);
    }
    public Vector3f transformVector(Vector3f vector)
    {
        double x = vector.x;
        double y = vector.y;
        double z = vector.z;
        var right = Vector3f.newXYZ(elements[0], elements[1], elements[2]).mul(x);
        var up = Vector3f.newXYZ(elements[4], elements[5], elements[6]).mul(y);
        var forward = Vector3f.newXYZ(elements[8], elements[9], elements[10]).mul(z);
        return right.add(up).add(forward);
    }
    public Matrix4f transponed()
    {
        var t = new double[16];
        for(int i = 0; i < 16; i++)
        {
            t[i] = elements[(i >> 2) + ((i & 3) << 2)];
        }
        return new Matrix4f(t);
    }

    static double at(double[] elements, int i, int j)
    {
        return elements[i + j * 4];
    }

    public double det()
    {
        double subFactor00 = at(elements, 2, 2) * at(elements, 3, 3) - at(elements, 3, 2) * at(elements, 2, 3);
		double subFactor01 = at(elements, 2, 1) * at(elements, 3, 3) - at(elements, 3, 1) * at(elements, 2, 3);
		double subFactor02 = at(elements, 2, 1) * at(elements, 3, 2) - at(elements, 3, 1) * at(elements, 2, 2);
		double subFactor03 = at(elements, 2, 0) * at(elements, 3, 3) - at(elements, 3, 0) * at(elements, 2, 3);
		double subFactor04 = at(elements, 2, 0) * at(elements, 3, 2) - at(elements, 3, 0) * at(elements, 2, 2);
		double subFactor05 = at(elements, 2, 0) * at(elements, 3, 1) - at(elements, 3, 0) * at(elements, 2, 1);

		double dcof0 = (at(elements, 1, 1) * subFactor00 - at(elements, 1, 2) * subFactor01 + at(elements, 1, 3) * subFactor02);
		double dcof1 = -(at(elements, 1, 0) * subFactor00 - at(elements, 1, 2) * subFactor03 + at(elements, 1, 3) * subFactor04);
		double dcof2 = (at(elements, 1, 0) * subFactor01 - at(elements, 1, 1) * subFactor03 + at(elements, 1, 3) * subFactor05);
		double dcof3 = -(at(elements, 1, 0) * subFactor02 - at(elements, 1, 1) * subFactor04 + at(elements, 1, 2) * subFactor05);

		return
			at(elements, 0, 0) * dcof0 + at(elements, 0, 1) * dcof1 +
			at(elements, 0, 2) * dcof2 + at(elements, 0, 3) * dcof3;
    }

    private static double step(double[] s, int i, int j, int si, int sj)
    {
        return s[((i + si) & 3) + (((j + sj) & 3) << 2)];
    }

    public Matrix4f inversed()
    {
        var result = new double[16];
        var det = det();
        for(int index = 0; index < 16; index++)
        {
            int i = index >> 2;
            int j = index & 3;
            double subdet = Matrix3f.det(m -> (float)step(elements, (m % 3) + i, (m / 3) + j, 1, 1));
            result[i] = subdet / det;
        }
        return new Matrix4f(result);
    }
    public Matrix4f lerp(Matrix4f b, double t)
    {
        var lerped = new double[16];
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
    public static Matrix4f from_COLLADA_Buffer(Buffer<Float> buffer, int start)
    {
        double[] elements = new double[16];
        for(int i = 0; i < 16; i++) elements[i] = buffer.at(i + start);
        return flipZY.mul(new Matrix4f(elements).transponed()).mul(flipZY);
    }
    public static Matrix4f fromArray(float[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        double[] es = new double[16];
        for(int i = 0; i < es.length; i++) es[i] = elements[i];
        return new Matrix4f(es);
    }
    public static Matrix4f fromArray(double[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        return new Matrix4f(elements);
    }
    public static Matrix4f fromArray(Float[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        double[] newElements = new double[16];
        for(int i = 0; i < 16; i++) newElements[i] = elements[i];
        return new Matrix4f(newElements);
    }
    public static Matrix4f fromArray(Double[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        double[] newElements = new double[16];
        for(int i = 0; i < 16; i++) newElements[i] = elements[i];
        return new Matrix4f(newElements);
    }
    public static Matrix4f fromArray_unsafe(double[] elements)
    {
        if(elements.length != 16) throw new RuntimeException("Wrong elements length");
        return new Matrix4f(elements);
    }
    public static Matrix4f fromIterable(Iterable<Double> iterable)
    {
        double[] elements = new double[16];
        var i = iterable.iterator();
        int c = 0;
        while(c < 16 && i.hasNext())
        {
            elements[c++] = i.next();
        }
        return new Matrix4f(elements);
    }
    public Matrix4f apply(ActionSingle<double[]> function)
    {
        double[] elements = this.elements.clone();
        function.call(elements);
        return new Matrix4f(elements);
    }
    public FloatBuffer applyToPointBuffer(FloatBuffer src, FloatBuffer dst)
    {
        int s = Math.min(src.capacity(), dst.capacity());
        for(int i = 0; i < s; i+=3)
        {
            Vector3f v = Vector3f.newXYZ(
                src.get(i),
                src.get(i + 1),
                src.get(i + 2));
            v = transformPoint(v);
            dst.put(i,     (float)v.x);
            dst.put(i + 1, (float)v.y);
            dst.put(i + 2, (float)v.z);
        }
        return dst;
    }
}