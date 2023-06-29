package nine.math;

import nine.buffer.Buffer;

public final class Vector3fBufferElement implements Vector3f
{
    Buffer<Float> buffer;
    int startIndex;
    int stride;

    public Vector3fBufferElement(Buffer<Float> buffer, int startIndex, int stride)
    {
        this.buffer = buffer;
        this.startIndex = startIndex;
        this.stride = stride;
    }
    public Vector3fBufferElement(Buffer<Float> buffer, int startIndex)
    {
        this.buffer = buffer;
        this.startIndex = startIndex;
        this.stride = 1;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        acceptor.call(
            buffer.at(startIndex),
            buffer.at(startIndex + stride),
            buffer.at(startIndex + stride + stride));
    }
}