package nine.math;

import nine.buffer.Buffer;

public class Matrix4fRowBuffer implements Matrix4f
{
    Elements elements;

    public Matrix4fRowBuffer(Buffer<Float> buffer)
    {
        int length = buffer.length();
        elements = i ->
        {
            i = (i >> 2) + ((i & 3) << 2);
            if(i < length) return buffer.at(i);
            return 0f;
        };
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        acceptor.call(elements);
    }
}