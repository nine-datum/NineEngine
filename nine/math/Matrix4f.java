package nine.math;

import nine.function.RefreshStatus;

public interface Matrix4f
{
    /*
        elements layout :

        [ 0  4  8  12 ] -- line 0
        [ 1  5  9  13 ] -- line 1
        [ 2  6  10 14 ] -- line 2
        [ 3  7  11 15 ] -- line 3

        [ 0  1  2  3  ] -- column 0
        [ 4  5  6  7  ] -- column 1
        [ 8  9  10 11 ] -- column 2
        [ 12 13 14 15 ] -- column 2
    */
    
    void accept(ElementsAcceptor acceptor);

    static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        return translation(position).mul(rotation(rotation)).mul(scale(scale));
    }
    static Matrix4f transform(Vector3f position, Vector3f rotation)
    {
        return translation(position).mul(rotation(rotation));
    }
    static Matrix4f rotation(Vector3f rotation)
    {
        return new Matrix4fRotationX(rotation.x())
            .mul(new Matrix4fRotationY(rotation.y()))
            .mul(new Matrix4fRotationZ(rotation.z()));
    }
    static Matrix4f scale(Vector3f scale)
    {
        return action -> scale.accept((x, y, z) -> action.call(index ->
        {
            switch(index)
            {
                case 0: return x;
                case 5: return y;
                case 10: return z;
                case 15: return 1f;
                default: return 0f;
            }
        }));
    }
    static Matrix4f translation(Vector3f translation)
    {
        return action -> translation.accept((tx, ty, tz) -> action.call(index ->
        {
            switch(index)
            {
                case 12: return tx;
                case 13: return ty;
                case 14: return tz;
                case 0:
                case 5:
                case 10:
                case 15: return 1f;
                default: return 0f;
            }
        }));
    }
    static Matrix4f orbitalCamera(Vector3f position, Vector3f rotation, ValueFloat distance)
    {
        return Matrix4f.translation(Vector3f.newZ(distance))
            .mul(Matrix4f.rotation(rotation.negative()))
            .mul(Matrix4f.translation(position.negative()));
    }
    static Matrix4f perspective(ValueFloat aspect, ValueFloat fov, ValueFloat near, ValueFloat far)
    {
        return action -> fov.accept(fovV -> aspect.accept(aspectV -> far.accept(farV -> near.accept(nearV -> action.call(index ->
        {
            float tan = (float)Math.tan(fovV * 0.5f);
            switch(index)
            {
                case 0: return 1f / (aspectV * tan);
                case 5: return 1f / tan;
                case 10: return (farV + nearV) / (farV - nearV);
                case 11: return 1f;
                case 14: return -2f * farV * nearV / (farV - nearV);
                default: return 0f;
            }
        })))));
    }

    default Matrix4f mul(Matrix4f m)
    {
        return action -> accept(ea -> m.accept(eb ->
        {
            action.call(index ->
            {
                int i = index & 3;
                int j = index >> 2;
                int startA = i;
                int startB = j << 2;

                return ea.at(startA) * eb.at(startB) +
                        ea.at(startA + 4) * eb.at(startB + 1) +
                        ea.at(startA + 8) * eb.at(startB + 2) +
                        ea.at(startA + 12) * eb.at(startB + 3);
            });
        }));
    }
    default Matrix4f transponed()
    {
        return action -> accept(e -> action.call(i -> e.at((i >> 2) + ((i & 3) << 2))));
    }
    default Matrix4fStruct struct()
    {
        return new Matrix4fStruct(this);
    }
    default Matrix4f cached(RefreshStatus refreshStatus)
    {
        var status = refreshStatus.make();
        var cache = new Matrix4fStruct();

        return action ->
        {
            if(status.mark())
            {
                cache.apply(this);
            }
            cache.accept(action);
        };
    }
    default String string()
    {
        return struct().toString();
    }
}