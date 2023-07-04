package nine.io.encoding;

import nine.function.ErrorHandler;
import nine.io.StorageResource;

public class FileEncoder
{
    StorageResource output;

    public FileEncoder(StorageResource output)
    {
        this.output = output;
    }

    public void encode(Encodable encodable, ErrorHandler errorHandler)
    {
        output.write(flow -> encodable.encode(bytes ->
        {
            try { flow.write(bytes); }
            catch (Throwable th) { errorHandler.call(th); }
        }), errorHandler);
    }
}
