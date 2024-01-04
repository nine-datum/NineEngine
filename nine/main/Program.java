package nine.main;

import nine.io.FileStorage;

public class Program
{
    public static void main(String[] args)
    {
        var storage = new FileStorage();
        storage.loadScript("resources/scripts/main.jena");
    }
}