package nine.game;

import nine.math.FloatFunc;
import nine.math.Vector3f;

public interface HumanInstance
{
    Human create(HumanController controller, Vector3f position, float rotation, FloatFunc time, FloatFunc deltaTime);
}