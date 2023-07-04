package nine.io;

import java.io.IOException;
import java.io.OutputStream;

import nine.function.ErrorPrinter;

public class OutputFlowFromStream implements OutputFlow
{
    OutputStream stream;

    public OutputFlowFromStream(OutputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public void write(byte b)
    {
        try { stream.write(b); }
        catch(IOException error) { ErrorPrinter.instance.call(error); }
    }
}