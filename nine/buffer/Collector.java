package nine.buffer;

public class Collector<T>
{
    ArrayMapping<T> mapping;

    public Collector(ArrayMapping<T> mapping)
    {
        this.mapping = mapping;
    }

    public T[] collect(Buffer<T> buffer)
    {
        int length = buffer.length();
        T[] array = mapping.map(length);
        for(int i = 0; i < length; i++) array[i] = buffer.at(i);
        return array;
    }
}