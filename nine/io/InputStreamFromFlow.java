package nine.io;

import java.io.InputStream;

public class InputStreamFromFlow extends InputStream
{
    private static Count one = new MinCount(1);
    private static ThreadLocal<Integer> temp = new ThreadLocal<Integer>();
    private static Output set = i -> temp.set(Byte.toUnsignedInt(i));

    private InputFlow flow;

    public InputStreamFromFlow(InputFlow flow)
    {
        this.flow = flow;
    }

    @Override
    public int read()
    {
        temp.set(-1);
        flow.read(one, set);
        return temp.get();
    }
}