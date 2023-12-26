package nine.geometry.collada;

public interface Animator
{
    Animation animation(String bone);

    static final Animator none = bone -> null;
}