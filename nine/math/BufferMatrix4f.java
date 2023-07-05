package nine.math;

import nine.buffer.Buffer;

public class BufferMatrix4f implements Matrix4f
{
    Elements elements;

    public BufferMatrix4f(Buffer<Float> buffer)
    {
        int length = buffer.length();
        elements = i ->
        {
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