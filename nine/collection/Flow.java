package nine.collection;

import nine.buffer.Buffer;
import nine.buffer.FlowToBuffer;

public interface Flow<T>
{
    interface FlowMapping<TIn, TOut>
    {
        void map(TIn in, FlowAction<TOut> action);
    }

    void read(FlowAction<T> action);


    default Buffer<T> collect()
    {
        return new FlowToBuffer<T>(this);
    }
    default<TOut> Flow<TOut> map(FlowMapping<T, TOut> mapping)
    {
        return action -> read(item -> mapping.map(item, action));
    }
    default Flow<T> concat(Flow<T> other)
    {
        return new ConcatFlow<T>(this, other);
    }
    @SafeVarargs
    static <T> Flow<T> of(T... items)
    {
        return new ArrayFlow<T>(items);
    }
}