package nine.buffer;

public class EmptyBuffer<T> implements Buffer<T>
{
    @Override
    public T at(int index)
    {
        throw new RuntimeException("Can't get element from an empty buffer");
    }

    @Override
    public int length()
    {
        return 0;
    }
}