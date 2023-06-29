package nine.buffer;

import nine.function.IntegerMapping;

public interface Translation<TIn, TOut>
{
    TOut translate(IntegerMapping<TIn> mapping, int index);
}