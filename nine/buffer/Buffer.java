package nine.buffer;

import java.util.Iterator;
import java.util.List;

import nine.collection.BufferToFlow;
import nine.collection.Flow;
import nine.collection.Mapping;
import nine.function.IntegerMapping;

public interface Buffer<T> extends IntegerMapping<T>
{
    int length();

    static<T> Buffer<T> empty()
    {
        return new EmptyBuffer<T>();
    }
    static Buffer<Integer> range(int length)
    {
        return new RangeBuffer(length);
    }
    static Buffer<Integer> range(int start, int length)
    {
        return new RangeBuffer(start, length);
    }
    @SafeVarargs
    static<T> Buffer<T> of(T... items)
    {
        return new ArrayBuffer<T>(items);
    }
    static<T> Buffer<T> of(List<T> list)
    {
        return new Buffer<T>()
        {
            @Override
            public T at(int i)
            {
                return list.get(i);
            }
            @Override
            public int length()
            {
                return list.size();
            }
        };
    }
    default<TOut> Buffer<TOut> map(Mapping<T, TOut> mapping)
    {
        return new MapBuffer<T, TOut>(this, mapping);
    }
    default Buffer<T> cached()
    {
        return new CachedBuffer<T>(this);
    }
    default Flow<T> flow()
    {
        return new BufferToFlow<T>(this);
    }
    default Iterable<T> iterable()
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return new Iterator<T>()
                {
                    int index = 0;
                    int length = length();

                    @Override
                    public boolean hasNext()
                    {
                        return index < length;
                    }

                    @Override
                    public T next()
                    {
                        return at(index++);
                    }
                };
            }
        };
    }
}