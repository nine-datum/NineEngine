package nine.io.encoding;

import nine.function.ErrorHandler;
import nine.io.ByteArrayOutput;
import nine.io.MinCount;
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
            flow.read(new MinCount(length), new ByteArrayOutput(buffer));
            return buffer;
        }),
        errorHandler);
    }
}