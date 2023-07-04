package nine.io;

import java.io.InputStream;

public class InputStreamFromFlow extends InputStream
{
    private InputFlow flow;

    public InputStreamFromFlow(InputFlow flow)
    {
        this.flow = flow;
    }

    @Override
    public int read()
    {
        return flow.read();
    }
}