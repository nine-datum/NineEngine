package nine.io;

import nine.function.ErrorHandler;

public interface StorageResource
{
    void read(InputFlowAcceptor acceptor, ErrorHandler errorHandler);
    void write(OutputFlowAcceptor acceptor, ErrorHandler errorHandler);
}