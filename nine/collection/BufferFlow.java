package nine.collection;

public class BufferFlow<T> implements Flow<T>
{
    T[] buffer;
    int start;
    int end;
    
    public BufferFlow(T[] buffer, int start, int count)
    {
        this.buffer = buffer;
        this.start = start;
        this.end = start + count;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        for(int i = start; i < end; i++)
        {
            action.call(buffer[i]);
        }
    }
}