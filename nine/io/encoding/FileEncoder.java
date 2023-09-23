package nine.io.encoding;

import nine.function.ErrorHandler;
import nine.io.ByteArrayInput;
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
            flow.write(new ByteArrayInput(bytes));
        }), errorHandler);
    }
}
