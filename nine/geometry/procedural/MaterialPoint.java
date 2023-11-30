package nine.geometry.procedural;

import nine.math.ValueFloat;
import nine.math.Vector3f;

public interface MaterialPoint
{
    interface Acceptor
    {
        void call(ValueFloat width, Vector3f coords, Vector3f normal);
    }
    void accept(Acceptor acceptor);

    static MaterialPoint of(ValueFloat width, Vector3f coords, Vector3f normal)
    {
        return action -> action.call(width, coords, normal);
    }
}