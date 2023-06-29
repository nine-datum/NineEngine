package nine.collection;

import nine.buffer.Buffer;

public class BufferToFlow<T> implements Flow<T>
{
    Buffer<T> buffer;

    public BufferToFlow(Buffer<T> buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        int length = buffer.length();
        for(int i = 0; i < length; i++) action.call(buffer.at(i));
    }
}