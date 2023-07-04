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
    @Override
    public int read(byte[] buffer, int start, int length)
    {
        ByteArrayOutput output = new ByteArrayOutput(buffer, start);
        flow.read(new MinCount(length), output);
        return output.status();
    }
    @Override
    public int read(byte[] buffer)
    {
        ByteArrayOutput output = new ByteArrayOutput(buffer);
        flow.read(new MinCount(buffer.length), output);
        return output.status();
    }
}