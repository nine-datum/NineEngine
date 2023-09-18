package nine.geometry.collada;

public interface BuffersReader
{
    void read(String source, String material, BufferMapping<Float> floats, BufferMapping<Integer> ints);
}