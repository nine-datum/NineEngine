package nine.game;

import nine.math.Vector3f;

public interface Body
{
    interface Modification
    {
        void modify(Vector3f position, Vector3f rotation, Modifyable modifyable);
    }
    interface Modifyable
    {
        void modify(Vector3f position, Vector3f rotation);
    }

    void modify(Modification modification);
}