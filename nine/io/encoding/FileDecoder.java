package nine.io.encoding;

import nine.function.ErrorHandler;
import nine.io.StorageResource;

public class FileDecoder
{
    StorageResource input;

    public FileDecoder(StorageResource input)
    {
        this.input = input;
    }

    public void decode(Decodable decodable, ErrorHandler errorHandler)
    {
        input.read(flow -> decodable.decode(length ->
        {
            byte[] buffer = new byte[length];
            try { flow.read(buffer); }
            catch (Throwable error) { errorHandler.call(error); }
            return buffer;
        }),
        errorHandler);
    }
}