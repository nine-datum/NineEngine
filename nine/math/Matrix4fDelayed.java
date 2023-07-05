package nine.math;

public class Matrix4fDelayed implements Matrix4f
{
    Matrix4fStruct cache = new Matrix4fStruct();
    Matrix4f source;
    float lastUpdateTime;
    float update;
    float delay;
    ValueFloat time = new Time();

    public Matrix4fDelayed(Matrix4f source, float delay)
    {
        this.source = source;
        this.delay = delay;
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        time.accept(t -> update = t);
        if(update >= lastUpdateTime + delay)
        {
            cache.apply(source);
            lastUpdateTime = update;
        }
        cache.accept(acceptor);
    }
}