package nine.math;

public class Matrix4fDet implements ValueFloat
{
    Matrix4f matrix;

    public Matrix4fDet(Matrix4f matrix)
    {
        this.matrix = matrix;
    }

    static float at(Elements elements, int i, int j)
    {
        return elements.at(i + j * 4);
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        matrix.accept(elements ->
        {
            float subFactor00 = at(elements, 2, 2) * at(elements, 3, 3) - at(elements, 3, 2) * at(elements, 2, 3);
    		float subFactor01 = at(elements, 2, 1) * at(elements, 3, 3) - at(elements, 3, 1) * at(elements, 2, 3);
    		float subFactor02 = at(elements, 2, 1) * at(elements, 3, 2) - at(elements, 3, 1) * at(elements, 2, 2);
    		float subFactor03 = at(elements, 2, 0) * at(elements, 3, 3) - at(elements, 3, 0) * at(elements, 2, 3);
    		float subFactor04 = at(elements, 2, 0) * at(elements, 3, 2) - at(elements, 3, 0) * at(elements, 2, 2);
    		float subFactor05 = at(elements, 2, 0) * at(elements, 3, 1) - at(elements, 3, 0) * at(elements, 2, 1);

    		float dcof0 = (at(elements, 1, 1) * subFactor00 - at(elements, 1, 2) * subFactor01 + at(elements, 1, 3) * subFactor02);
    		float dcof1 = -(at(elements, 1, 0) * subFactor00 - at(elements, 1, 2) * subFactor03 + at(elements, 1, 3) * subFactor04);
    		float dcof2 = (at(elements, 1, 0) * subFactor01 - at(elements, 1, 1) * subFactor03 + at(elements, 1, 3) * subFactor05);
    		float dcof3 = -(at(elements, 1, 0) * subFactor02 - at(elements, 1, 1) * subFactor04 + at(elements, 1, 2) * subFactor05);

    		acceptor.call(
    				at(elements, 0, 0) * dcof0 + at(elements, 0, 1) * dcof1 +
    				at(elements, 0, 2) * dcof2 + at(elements, 0, 3) * dcof3);
        });
    }
}