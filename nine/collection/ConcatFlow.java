package nine.collection;

public class ConcatFlow<T> implements Flow<T>
{
    Flow<T>[] collections;

    @SafeVarargs
    public ConcatFlow(Flow<T>... collections)
    {
        this.collections = collections;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        for(Flow<T> collection : collections) collection.read(action);
    }
}