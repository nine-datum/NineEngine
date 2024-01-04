package nine.opengl;

public class EmptyUniform<T> implements Uniform<T>
{
    @Override
    public void load(T value) { }
}