package nine.input;

public interface Key
{
    boolean isDown();
    boolean isUp();

    static Key or (Key a, Key b)
    {
        return new Key()
        {
            public boolean isDown() { return a.isDown() || b.isDown(); };
            public boolean isUp() { return a.isUp() || b.isUp(); };
        };
    }
    static Key and (Key a, Key b)
    {
        return new Key()
        {
            public boolean isDown() { return a.isDown() && b.isDown(); };
            public boolean isUp() { return a.isUp() && b.isUp(); };
        };
    }
}