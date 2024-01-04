package nine.geometry.procedural;

import nine.math.Vector3f;

public interface MaterialPoint
{
    interface Acceptor
    {
        void call(float width, Vector3f coords, Vector3f normal);
    }
    void accept(Acceptor acceptor);

    static MaterialPoint of(float width, Vector3f coords, Vector3f normal)
    {
        return action -> action.call(width, coords, normal);
    }
}