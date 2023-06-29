package nine.buffer;

public class RangeBuffer implements Buffer<Integer>
{
    int start;
    int length;

    public RangeBuffer(int start, int length)
    {
        this.start = start;
        this.length = length;
    }

    @Override
    public Integer at(int index)
    {
        return Integer.valueOf(start + index);
    }

    @Override
    public int length()
    {
        return length;
    }
}