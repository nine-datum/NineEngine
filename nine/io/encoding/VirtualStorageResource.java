package nine.io.encoding;

import nine.function.ErrorHandler;
import nine.io.ByteArrayInputFlow;
import nine.io.InputFlowAcceptor;
import nine.io.OutputFlowAcceptor;
import nine.io.StorageResource;

public class VirtualStorageResource implements StorageResource
{
    byte[] bytes;

    public VirtualStorageResource(byte[] bytes)
    {
        this.bytes = bytes;
    }

    @Override
    public void read(InputFlowAcceptor acceptor, ErrorHandler errorHandler)
    {
        acceptor.call(new ByteArrayInputFlow(bytes));
    }

    @Override
    public void write(OutputFlowAcceptor acceptor, ErrorHandler errorHandler)
    {
        errorHandler.call(new Exception("Can't write to virtual storage"));
    }
}