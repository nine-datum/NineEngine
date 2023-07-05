package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.buffer.EmptyBuffer;

public class EmptyBufferMapping<T> implements BufferMapping<T>
{
    Buffer<T> empty = new EmptyBuffer<T>();
    @Override
    public Buffer<T> map(String semantic)
    {
        return empty;
    }
}