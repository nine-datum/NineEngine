package nine.buffer;

public class FloatArrayBuffer implements Buffer<Float>
{
    float[] array;

    public FloatArrayBuffer(float... array)
    {
        this.array = array;
    }

    @Override
    public Float at(int i)
    {
        return Float.valueOf(array[i]);
    }

    @Override
    public int length()
    {
        return array.length;
    }
}