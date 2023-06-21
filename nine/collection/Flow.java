package nine.collection;

public interface Flow<T>
{
    void read(FlowAction<T> action);
}