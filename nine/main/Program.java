package nine.main;

import jena.lang.value.NoneValue;
import nine.io.FileStorage;

public class Program
{
    public static void main(String[] args)
    {
        var storage = new FileStorage();
        storage.loadScript("scripts/main.jena").call(NoneValue.instance);
    }
}