package nine.math;

import nine.buffer.Buffer;

public class Matrix4fRowBuffer implements Matrix4f
{
    Elements elements;

    public Matrix4fRowBuffer(Buffer<Float> buffer, int offset)
    {
        elements = i ->
        {
            i = (i >> 2) + ((i & 3) << 2);
            return buffer.at(offset + i);
        };
    }
    public Matrix4fRowBuffer(Buffer<Float> buffer)
    {
        this(buffer, 0);
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        acceptor.call(elements);
    }
}