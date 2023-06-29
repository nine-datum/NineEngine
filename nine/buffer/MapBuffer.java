package nine.buffer;

import nine.collection.Mapping;

public class MapBuffer<TIn, TOut> implements Buffer<TOut>
{
    Buffer<TIn> source;
    Mapping<TIn, TOut> mapping;

    public MapBuffer(Buffer<TIn> source, Mapping<TIn, TOut> mapping)
    {
        this.source = source;
        this.mapping = mapping;
    }

    @Override
    public TOut at(int index)
    {
        return mapping.map(source.at(index));
    }

    @Override
    public int length()
    {
        return source.length();
    }
}