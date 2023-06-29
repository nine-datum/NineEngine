package nine.buffer;

public class IntegerArrayBuffer implements Buffer<Integer>
{
    int[] array;

    public IntegerArrayBuffer(int... array)
    {
        this.array = array;
    }

    @Override
    public Integer at(int i)
    {
        return Integer.valueOf(array[i]);
    }

    @Override
    public int length()
    {
        return array.length;
    }
}