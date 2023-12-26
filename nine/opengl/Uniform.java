package nine.opengl;

public interface Uniform
{
    void load();

    static Uniform of(Uniform... list)
    {
        return new CompositeUniform(list);
    }
}