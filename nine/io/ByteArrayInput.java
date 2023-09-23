package nine.io;

public class ByteArrayInput implements Input
{
    private byte[] array;
    private int start;
    private int end;

    public ByteArrayInput(byte[] array, int start, int count)
    {
        this.array = array;
        this.start = start;
        this.end = start + count;
    }
    public ByteArrayInput(byte[] array)
    {
        this.array = array;
        this.end = array.length;
    }

    @Override
    public void in(Output out)
    {
        for(int i = start; i < end; i++) out.out(array[i]);
    }
}