package nine.function;

public class InoutAction<TIn, TOut>
{
    TOut result;

    public InoutAction(TIn in, ActionDouble<TIn, ActionSingle<TOut>> action)
    {
        action.call(in, r -> result = r);
    }

    public TOut result()
    {
        return result;
    }
}