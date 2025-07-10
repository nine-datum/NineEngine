package nine.opengl.shader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nine.io.TextFileReader;
import nine.io.StorageResource;
import nine.opengl.ShaderSource;
import nine.opengl.ShaderSourceAcceptor;

public class FileShaderSource implements ShaderSource
{
    StorageResource file;
    ShaderProcess process;
    ShaderMacro[] macros;

    public FileShaderSource(StorageResource file, ShaderProcess process, ShaderMacro... macros)
    {
        this.file = file;
        this.process = process;
        this.macros = macros;
    }

    @Override
    public void accept(ShaderSourceAcceptor acceptor)
    {
        List<String> appendStart = new ArrayList<String>();
        List<String> prependStart = new ArrayList<String>();
        ShaderEditableSource editable = new ShaderEditableSource()
        {
            @Override
            public void appendStart(String line)
            {
                appendStart.add(line);
            }
            @Override
            public void prependStart(String line)
            {
                prependStart.add(line);
            }
        };
        for(ShaderMacro macro : macros) macro.edit(editable);
        String header = String.join("\n", Stream.concat(prependStart.stream(), appendStart.stream()).collect(Collectors.toList()));
        new TextFileReader(file).read(lines ->
        {
            String source = String.join("\n", process.process(lines));
            acceptor.call(String.join("\n", header, source));
        },
        System.out::println);
    }
}
