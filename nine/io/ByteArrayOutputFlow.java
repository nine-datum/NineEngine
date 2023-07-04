package nine.io;

public class ByteArrayOutputFlow implements OutputFlow
{
    byte[] array;
    private int position;

    public ByteArrayOutputFlow(byte[] array)
    {
        this.array = array;
    }

    @Override
    public void write(byte b)
    {
        array[position++] = b;
    }
}