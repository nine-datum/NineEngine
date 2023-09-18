package nine.collection;

public class UnrollFlow<T> implements Flow<T>
{
    Flow<? extends Flow<T>> source;

    public UnrollFlow(Flow<? extends Flow<T>> in)
    {
        this.source = in;
    }
    
    @Override
    public void read(FlowAction<T> action)
    {
        source.read(i -> i.read(action));
    }
}