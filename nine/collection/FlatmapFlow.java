package nine.collection;

public class FlatmapFlow<TIn, TOut> implements Flow<TOut>
{
    Flow<TIn> in;
    Mapping<TIn, Flow<TOut>> mapping;

    public FlatmapFlow(Flow<TIn> in, Mapping<TIn, Flow<TOut>> mapping)
    {
        this.in = in;
        this.mapping = mapping;
    }
    
    @Override
    public void read(FlowAction<TOut> action)
    {
        in.read(i -> mapping.map(i).read(action));
    }
}