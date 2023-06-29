package nine.buffer;

public class FloatCollector
{
    public float[] collect(Buffer<Float> buffer)
    {
        int length = buffer.length();
        float[] array = new float[length];
        for(int i = 0; i < length; i++) array[i] = buffer.at(i);
        return array;
    }
}