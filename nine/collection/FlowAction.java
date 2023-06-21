package nine.collection;

public interface FlowAction<T>
{
    void call(T item);
}