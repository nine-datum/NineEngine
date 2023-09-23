package nine.io;

import java.io.IOException;
import java.io.InputStream;

public class InputFlowFromStream implements InputFlow
{
    InputStream stream;

    public InputFlowFromStream(InputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public void read(Count count, Output output) {
        try
        {
            int r;
            int c = count.count(Integer.MAX_VALUE);
            byte[] buffer = new byte[c];
            r = stream.read(buffer);
            for(int i = 0; i < r; i++) output.out(buffer[i]);
        }
        catch(IOException error) {}
    }
}