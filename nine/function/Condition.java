package nine.function;

public interface Condition<T>
{
    boolean match(T item);
    
    static<T> Condition<T> equality(T item)
    {
    	return i -> i.equals(item);
    }
}