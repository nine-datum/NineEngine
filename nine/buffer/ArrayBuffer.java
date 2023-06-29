package nine.buffer;

public class ArrayBuffer<T> implements Buffer<T>
{
    T[] array;

    @SafeVarargs
    public ArrayBuffer(T... array)
    {
        this.array = array;
    }

    @Override
    public T at(int index)
    {
        return array[index];
    }

    @Override
    public int length()
    {
        return array.length;
    }
}