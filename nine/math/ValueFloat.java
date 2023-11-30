package nine.math;

public interface ValueFloat
{
    void accept(FloatAcceptor acceptor);

    default ValueFloat add(ValueFloat b)
    {
        return action -> accept(av -> b.accept(bv -> action.call(av + bv)));
    }
    default ValueFloat sub(ValueFloat b)
    {
        return action -> accept(av -> b.accept(bv -> action.call(av - bv)));
    }
    default ValueFloat mul(ValueFloat b)
    {
        return action -> accept(av -> b.accept(bv -> action.call(av * bv)));
    }
    default ValueFloat div(ValueFloat b)
    {
        return action -> accept(av -> b.accept(bv -> action.call(av / bv)));
    }
    default ValueFloat sin()
    {
        return action -> accept(v -> action.call((float)Math.sin(v)));
    }
    default ValueFloat cos()
    {
        return action -> accept(v -> action.call((float)Math.cos(v)));
    }
    public static ValueFloat pi()
    {
        return action -> action.call((float)Math.PI);
    }
    default ValueFloat degreesToRadians()
    {
        return action -> accept(degrees -> action.call(degrees * (float)Math.PI * (1f / 180f)));
    }
    public static ValueFloat vector2fAngle(Vector2f vector)
    {
        return action -> vector.accept((x, y) ->
        {
            if (x == 0f && y == 0f) action.call(0f);
            else
            {
			    action.call((float)Math.acos(x) * (y > 0 ? 1f : -1f));
            }
        });
    }
    public static ValueFloat of(float v)
    {
        return new ValueFloatStruct(v);
    }
}