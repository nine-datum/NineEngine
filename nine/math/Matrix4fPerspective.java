package nine.math;

public final class Matrix4fPerspective implements Matrix4f
{
    ValueFloat aspect;
    ValueFloat fov;
    ValueFloat far;
    ValueFloat near;

    public Matrix4fPerspective(ValueFloat aspect, ValueFloat fov, ValueFloat near, ValueFloat far) {
        this.aspect = aspect;
        this.fov = fov;
        this.far = far;
        this.near = near;
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        fov.accept(fov -> aspect.accept(aspect -> far.accept(far -> near.accept(near -> acceptor.call(index ->
        {
            float tan = (float)Math.tan(fov * 0.5f);
            switch(index)
            {
                case 0: return 1f / (aspect * tan);
                case 5: return 1f / tan;
                case 10: return (far + near) / (far - near);
                case 11: return 1f;
                case 14: return -2f * far * near / (far - near);
                default: return 0f;
            }
        })))));
    }
}