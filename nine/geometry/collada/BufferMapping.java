package nine.geometry.collada;

import nine.buffer.Buffer;

public interface BufferMapping<T>
{
    Buffer<T> map(String semantic);
}