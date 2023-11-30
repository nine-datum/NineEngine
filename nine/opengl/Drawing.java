package nine.opengl;

public interface Drawing
{
    void draw();

    static Drawing empty()
    {
        return () -> { };
    }
}