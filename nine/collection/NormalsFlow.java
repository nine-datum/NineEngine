package nine.collection;

import nine.buffer.Buffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.math.Vector3f;

public class NormalsFlow implements Flow<Float>
{
    Flow<Vector3f> normals;

    public NormalsFlow(Buffer<Float> positions)
    {
        Buffer<Vector3f> vertices = new MapBuffer<Integer, Vector3f>(
            new RangeBuffer(positions.length() / 3),
            i -> Vector3f.newBuffer(positions, i * 3));

        normals = new FlatmapFlow<Integer, Vector3f>(
            new RangeFlow(vertices.length() / 3),
            i -> action ->
            {
                Vector3f a = vertices.at(i * 3);
                Vector3f b = vertices.at(i * 3 + 1);
                Vector3f c = vertices.at(i * 3 + 2);
                Vector3f normal = b.sub(a).cross(c.sub(a)).normalized();
                action.call(normal);
                action.call(normal);
                action.call(normal);
            }
        );
    }

    @Override
    public void read(FlowAction<Float> action)
    {
        normals.read(n -> n.accept((x, y, z) ->
        {
            action.call((float)x);
            action.call((float)y);
            action.call((float)z);
        }));
    }
}