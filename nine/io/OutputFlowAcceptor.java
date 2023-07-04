package nine.io;

import java.io.OutputStream;

public interface OutputFlowAcceptor
{
    void call(OutputStream outputFlow);
}