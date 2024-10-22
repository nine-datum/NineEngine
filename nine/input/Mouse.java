package nine.input;

import nine.math.Vector2f;

public interface Mouse
{
    Vector2f position();
    Vector2f delta();
    Key left();
    Key right();
    Key middle();
}