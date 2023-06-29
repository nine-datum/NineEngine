package nine.buffer;

public interface Buffer<T>
{
    T at(int index);    
    int length();
}
