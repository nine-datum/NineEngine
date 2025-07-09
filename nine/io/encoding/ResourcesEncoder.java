package nine.io.encoding;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import nine.io.FileStorageResource;
import nine.io.InputStreamFromFlow;
import nine.io.StorageResource;

public class ResourcesEncoder implements Encodable
{
    Map<String, StorageResource> resources;

    public ResourcesEncoder(String path)
    {
        File root = new File(path);
        var abs = Paths.get(root.getAbsolutePath());
        try
        {
            resources = Files.find(abs, 1024, (a, b) -> b.isRegularFile())
              .map(p -> path + "/" + abs.relativize(p).toString())
              .collect(Collectors.toMap(file -> file, file -> new FileStorageResource(file)));
        }
        catch(Throwable error)
        {
            System.out.println(error);
        }
    }

    @Override
    public void encode(EncodingStream stream)
    {
        stream.writeInt(resources.size());
        resources.forEach((name, resource) ->
        {
            System.out.println("Resource encoded : " + name);
            resource.read(input ->
            {
                try(InputStream inputStream = new InputStreamFromFlow(input))
                {
                    byte[] bytes = inputStream.readAllBytes();

                    stream.writeText(name);
                    stream.writeByteArray(bytes);
                }
                catch(Throwable error)
                {
                    System.out.println(error);
                }
            },
            System.out::println);
        });
    }
}
