package nine.buffer;

public class ReverseBuffer<T> implements Buffer<T>
{
    Buffer<T> source;

    public ReverseBuffer(Buffer<T> source)
    {
        this.source = source;
    }

    @Override
    public T at(int i)
    {
        return source.at(source.length() - i - 1);
    }

    @Override
    public int length()
    {
        return source.length();
    }
}