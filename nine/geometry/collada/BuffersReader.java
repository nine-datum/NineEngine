package nine.geometry.collada;

public interface BuffersReader
{
    void read(String source, BufferMapping<Float> floats, BufferMapping<Integer> ints);
}