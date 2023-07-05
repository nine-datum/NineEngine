package nine.math;

public class Matrix4fStruct implements Matrix4f
{
    float[] array = new float[16];
    Elements elements = i -> array[i];

    public Matrix4fStruct()
    {
        array[0] = array[5] = array[10] = array[15] = 1f;
    }
    public Matrix4fStruct(Matrix4f matrix)
    {
        matrix.accept(e ->
        {
            for(int i = 0; i < 16; i++) array[i] = e.at(i);
        });
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        acceptor.call(elements);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        new Matrix4fPrinter(sb::append).print(this);
        return sb.toString();
    }
}