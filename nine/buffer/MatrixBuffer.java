package nine.buffer;

import nine.math.Matrix4f;

public class MatrixBuffer implements Buffer<Matrix4f>
{
    Buffer<Matrix4f> buffer;

    public MatrixBuffer(String text)
    {
        this(new TextValueBuffer<>(text, Float::parseFloat));
    }
    public MatrixBuffer(Buffer<Float> values)
    {
        buffer = new CachedBuffer<>(
            new MapBuffer<>(
                new RangeBuffer(values.length() / 16),
                i -> Matrix4f.from_COLLADA_Buffer(values, i * 16)));
    }

    @Override
    public Matrix4f at(int i)
    {
        return buffer.at(i);
    }

    @Override
    public int length()
    {
        return buffer.length();
    }
}