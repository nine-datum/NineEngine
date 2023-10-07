package nine.math;

public final class Vector3fNormalized implements Vector3f
{
    Vector3f source;
    ValueFloat length;

    public Vector3fNormalized(Vector3f source)
    {
        this.source = source;
        length = source.length();
    }

    @Override
    public void accept(XYZAction acceptor)
    {
        length.accept(len -> source.accept((x, y, z) ->
        {
            if (len != 0)
                acceptor.call(x / len, y / len, z / len);
            else
                acceptor.call(0f, 0f, 0f);
        }));
    }
    @Override
    public Vector3f normalized()
    {
        return this;
    }
}