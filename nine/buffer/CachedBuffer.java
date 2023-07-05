package nine.buffer;

public class CachedBuffer<T> implements Buffer<T>
{
    Object[] array;

    public CachedBuffer(Buffer<T> source)
    {
        this.array = new Object[source.length()];
        for(int i = 0; i < array.length; i++) array[i] = source.at(i);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T at(int i)
    {
        return (T)array[i];
    }

    @Override
    public int length()
    {
        return array.length;
    }
}