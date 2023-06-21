package nine.collection;

public interface Mapping<TIn, TOut>
{
    TOut map(TIn in);
}