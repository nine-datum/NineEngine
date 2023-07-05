package nine.math;

public class Matrix4fInversed implements Matrix4f
{
    private Matrix4f source;
    private ValueFloat det;

    public Matrix4fInversed(Matrix4f source)
    {
        this.source = source;
        det = new Matrix4fDet(source);
    }

    public static float step(Elements s, int i, int j, int si, int sj)
    {
        return s.at(((i + si) & 3) + (((j + sj) & 3) << 2));
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        det.accept(det -> source.accept(s -> acceptor.call(index ->
        {
            // because adj matrix has to be transponed. implicitly transponing here
            int i = index >> 2;
            int j = index & 3;
            Matrix3f detMatrix = a -> a.call(m -> step(s, (m % 3) + i, (m / 3) + j, 1, 1));
            float[] box = { 0f };
            new Matrix3fDet(detMatrix).accept(d -> box[0] = d);
            return box[0] / det;
        })));
    }
}