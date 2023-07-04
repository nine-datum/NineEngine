package nine.buffer;

import nine.collection.ArrayFlow;
import nine.collection.FilterFlow;
import nine.collection.MapFlow;
import nine.collection.Mapping;

public class TextValueBuffer<T> implements Buffer<T> 
{
    Buffer<T> buffer;

    public TextValueBuffer(String text, Mapping<String, T> conversion)
    {
        buffer = new FlowToBuffer<>(
            new MapFlow<>(
                new FilterFlow<>(
                    new ArrayFlow<>(text.split(" ")),
                    s -> s.length() != 0),
                conversion));
    }

    @Override
    public T at(int i)
    {
        return buffer.at(i);
    }

    @Override
    public int length()
    {
        return buffer.length();
    }
}