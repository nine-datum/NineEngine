package nine.collection;

public class MapFlow<TIn, TOut> implements Flow<TOut>
{
    Flow<TIn> in;
    Mapping<TIn, TOut> mapping;

    public MapFlow(Flow<TIn> in, Mapping<TIn, TOut> mapping)
    {
        this.in = in;
        this.mapping = mapping;
    }
    @Override
    public void read(FlowAction<TOut> action)
    {
        in.read(i -> action.call(mapping.map(i)));
    }
}