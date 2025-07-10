package nine.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nine.function.ActionSingle;
import nine.function.ErrorHandler;

public class TextFileReader
{
    private StorageResource file;
    public TextFileReader(StorageResource file)
    {
        this.file = file;
    }
    public void read(ActionSingle<Iterable<String>> scannerAcceptor, ErrorHandler errorHandler)
    {
        file.read(flow ->
        {
            Scanner scanner = new Scanner(new InputStreamFromFlow(flow));
            List<String> lines = new ArrayList<String>();
            while(scanner.hasNextLine())
            {
                lines.add(scanner.nextLine());
            }
            scanner.close();
            scannerAcceptor.call(lines);
        }, errorHandler);
    }
    public String readString () {
      List<String> lines = new ArrayList<String>();
      file.read(flow ->
      {
          Scanner scanner = new Scanner(new InputStreamFromFlow(flow));
          while(scanner.hasNextLine())
          {
              lines.add(scanner.nextLine());
          }
          scanner.close();
      }, e -> { throw new RuntimeException(e); });
      return String.join("\n", lines);
    }
}
