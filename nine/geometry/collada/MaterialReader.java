package nine.geometry.collada;

import nine.drawing.Color;

public interface MaterialReader
{
    void call(String name, String textureFile, Color color);
}