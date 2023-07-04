package nine.io;

import java.io.InputStream;

public interface InputFlowAcceptor
{
    void call(InputStream inputFlow);
}