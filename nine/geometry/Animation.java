package nine.geometry;

import nine.function.RefreshStatus;
import nine.math.Matrix4f;

public interface Animation
{
    Matrix4f animate(float time);

    static final Animation none = time -> Matrix4f.identity;

    default Animation refreshable(RefreshStatus status)
    {
        var refresh = status.make();
        Matrix4f[] last = { null };
        return time ->
        {
            if(last[0] == null || refresh.mark())
            {
                return last[0] = animate(time);
            }
            else return last[0];
        };
    }
}