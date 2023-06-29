package nine.buffer;

import nine.function.IntField;

public class TranslateBuffer<TIn, TOut> implements Buffer<TOut>
{
    Buffer<TIn> source;
    IntField count;
    Translation<TIn, TOut> translation;
    
    public TranslateBuffer(Buffer<TIn> source, IntField count, Translation<TIn, TOut> translation)
    {
        this.source = source;
        this.count = count;
        this.translation = translation;
    }

    @Override
    public TOut at(int index)
    {
        return translation.translate(source, count.at(index));
    }
    @Override
    public int length()
    {
        return count.at(source.length());
    }
}