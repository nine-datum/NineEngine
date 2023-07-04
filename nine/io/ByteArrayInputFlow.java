package nine.io;

public class ByteArrayInputFlow implements InputFlow
{
    private byte[] array;
    private int position;

    public ByteArrayInputFlow(byte[] array)
    {
        this.array = array;
    }

    @Override
    public int read()
    {
        if(position < array.length) return array[position++];
        return -1;
    }
}