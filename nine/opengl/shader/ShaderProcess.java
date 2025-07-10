package nine.opengl.shader;

import java.util.ArrayList;

public interface ShaderProcess {
  Iterable<String> process(Iterable<String> lines);

  static final ShaderProcess explicitLayout = lines -> {
    int counter = 0;
    ArrayList<String> results = new ArrayList<String>();
    for (String line : lines) {
      if (line.startsWith("in ")) {
        results.add(String.format("layout (location = %d) %s", counter++, line));
      } else {
        results.add(line);
      }
    }
    return results;
  };
}
