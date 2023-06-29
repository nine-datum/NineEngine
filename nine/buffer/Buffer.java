package nine.buffer;

import nine.function.IntegerMapping;

public interface Buffer<T> extends IntegerMapping<T>
{
    int length();
}