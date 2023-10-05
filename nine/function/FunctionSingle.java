package nine.function;

public interface FunctionSingle<TArg, TResult>
{
    TResult call(TArg arg);
}