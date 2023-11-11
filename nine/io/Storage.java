package nine.io;

import jena.lang.source.Source;
import jena.lang.value.Value;

public interface Storage
{
    StorageResource open(String path);

    default Value loadScript(String path)
    {
        Value[] value = { null };
        open(path).read(input ->
        {
            value[0] = Value.of(Source.of(path, new InputStreamFromFlow(input)));
        },
        System.out::println);
        if(value[0] == null) throw new RuntimeException(String.format("Error loading script from file %s", path));
        return value[0];
    }
}