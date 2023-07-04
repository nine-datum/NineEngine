package nine.function;

public interface Condition<T>
{
    boolean match(T item);
}