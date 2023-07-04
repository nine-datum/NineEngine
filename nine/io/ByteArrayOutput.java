package nine.io;

public class ByteArrayOutput implements Output
{
    private int position;
    private int offset;
    private byte[] array;

    public ByteArrayOutput(byte[] array)
    {
        this.array = array;
    }

    public ByteArrayOutput(byte[] array, int offset)
    {
        this.array = array;
        this.offset = offset;
    }

    @Override
    public void out(byte b)
    {
        array[offset + position++] = b;
    }

    public int status()
    {
        return position == 0 ? -1 : position;
    }
}