package nine.io;

import java.io.IOException;
import java.io.InputStream;

import nine.function.ErrorPrinter;

public class InputFlowFromStream implements InputFlow
{
    InputStream stream;

    public InputFlowFromStream(InputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public int read() {
        try { return stream.read(); }
        catch(IOException error) { ErrorPrinter.instance.call(error); }
        return -1;
    }
}