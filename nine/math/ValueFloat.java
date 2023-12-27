package nine.math;

import nine.function.RefreshStatus;

public interface ValueFloat
{
    class Struct implements ValueFloat
    {
        public float value;

        Struct(float value)
        {
            this.value = value;
        }

        @Override
        public void accept(FloatAcceptor acceptor)
        {
            acceptor.call(value);
        }
    }

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
    default ValueFloat add(float b)
    {
        return action -> accept(av -> action.call(av + b));
    }
    default ValueFloat sub(float b)
    {
        return action -> accept(av -> action.call(av - b));
    }
    default ValueFloat mul(float b)
    {
        return action -> accept(av -> action.call(av * b));
    }
    default ValueFloat div(float b)
    {
        return action -> accept(av -> action.call(av / b));
    }
    default ValueFloat sin()
    {
        return action -> accept(v -> action.call((float)Math.sin(v)));
    }
    default ValueFloat cos()
    {
        return action -> accept(v -> action.call((float)Math.cos(v)));
    }
    default ValueFloat negative()
    {
        return action -> accept(v -> action.call(-v));
    }
    public static ValueFloat pi()
    {
        return action -> action.call((float)Math.PI);
    }
    default ValueFloat degreesToRadians()
    {
        return action -> accept(degrees -> action.call(degrees * (float)Math.PI * (1f / 180f)));
    }
    default float value()
    {
        float[] result = { 0f };
        accept(f -> result[0] = f);
        return result[0];
    }
    default ValueFloat delta(RefreshStatus refreshStatus)
    {
        return new Delta(this, refreshStatus);
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
    public static Struct of(float v)
    {
        return new Struct(v);
    }
}