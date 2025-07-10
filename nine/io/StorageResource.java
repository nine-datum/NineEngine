package nine.io;

import nine.function.ErrorHandler;
import java.io.InputStream;
import java.io.OutputStream;

public interface StorageResource
{
    void read(InputFlowAcceptor acceptor, ErrorHandler errorHandler);
    void write(OutputFlowAcceptor acceptor, ErrorHandler errorHandler);

    default InputStream inputStream() {
      InputStream[] box = { null };
      read(flow -> box[0] = new InputStreamFromFlow(flow), ErrorHandler.rethrow);
      return box[0];
    }
    default OutputStream outputStream() {
      OutputStream[] box = { null };
      write(flow -> box[0] = new OutputStreamFromFlow(flow), ErrorHandler.rethrow);
      return box[0];
    }
}
