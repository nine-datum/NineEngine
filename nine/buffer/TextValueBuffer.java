package nine.buffer;

import nine.collection.Mapping;
public class TextValueBuffer<T> implements Buffer<T> 
{
    Buffer<T> buffer;

    public TextValueBuffer(String text, Mapping<String, T> conversion)
    {
        buffer = new CachedBuffer<>(
            new MapBuffer<>(
                new TextElementsBuffer(text),
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