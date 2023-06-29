package nine.collection;

import nine.buffer.Buffer;
import nine.buffer.MapBuffer;
import nine.buffer.RangeBuffer;
import nine.math.Vector3f;
import nine.math.Vector3fBufferElement;
import nine.math.Vector3fCross;
import nine.math.Vector3fNormalized;
import nine.math.Vector3fSub;

public class NormalsFlow implements Flow<Float>
{
    Flow<Vector3f> normals;

    public NormalsFlow(Buffer<Float> positions)
    {
        Buffer<Vector3f> vertices = new MapBuffer<Integer, Vector3f>(
            new RangeBuffer(positions.length() / 3),
            i -> new Vector3fBufferElement(positions, i * 3));

        normals = new FlatmapFlow<Integer, Vector3f>(
            new RangeFlow(vertices.length() / 3),
            i -> action ->
            {
                Vector3f a = vertices.at(i);
                Vector3f b = vertices.at(i + 1);
                Vector3f c = vertices.at(i + 2);
                Vector3f normal = new Vector3fNormalized(new Vector3fCross(new Vector3fSub(b, a), new Vector3fSub(c, a)));
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
            action.call(x);
            action.call(y);
            action.call(z);
        }));
    }
}