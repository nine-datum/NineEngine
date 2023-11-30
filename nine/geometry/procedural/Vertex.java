package nine.geometry.procedural;

import nine.math.Vector2f;
import nine.math.Vector3f;

public interface Vertex
{
    interface Acceptor
    {
        void call(Vector3f position, Vector2f uv, Vector3f normal);
    }
    void accept(Acceptor acceptor);

    static Vertex of(Vector3f position, Vector2f uv, Vector3f normal)
    {
        return a -> a.call(position, uv, normal);
    }
}