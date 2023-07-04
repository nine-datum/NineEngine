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
            int pos = 0;
            int r;
            while(pos < length && (r = flow.read()) != -1) buffer[pos++] = (byte)r;
            return buffer;
        }),
        errorHandler);
    }
}