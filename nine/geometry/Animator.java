package nine.geometry;

public interface Animator
{
    Animation animation(String id, String name);

    static final Animator none = (id, name) -> null;
}