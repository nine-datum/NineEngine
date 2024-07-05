package nine.geometry;

public interface Animator
{
    Animation animation(String bone);

    static final Animator none = bone -> null;
}