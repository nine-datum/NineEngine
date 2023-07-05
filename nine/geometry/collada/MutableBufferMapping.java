package nine.geometry.collada;

import java.util.HashMap;

import nine.buffer.Buffer;
import nine.buffer.EmptyBuffer;

public class MutableBufferMapping<T> implements BufferMapping<T>
{
    HashMap<String, Buffer<T>> map;
    Buffer<T> empty = new EmptyBuffer<T>();

    public MutableBufferMapping()
    {
        map = new HashMap<String, Buffer<T>>();
    }
    public MutableBufferMapping(HashMap<String, Buffer<T>> map)
    {
        this.map = map;
    }

    @Override
    public Buffer<T> map(String semantic)
    {
        Buffer<T> b = map.get(semantic);
        if(b == null) b = empty;
        return b;
    }

    public void write(String semantic, Buffer<T> b)
    {
        map.put(semantic, b);
    }
}