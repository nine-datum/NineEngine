package nine.buffer;

public class IntegerCollector
{
    public int[] collect(Buffer<Integer> buffer)
    {
        int length = buffer.length();
        int[] array = new int[length];
        for(int i = 0; i < length; i++) array[i] = buffer.at(i);
        return array;
    }
}