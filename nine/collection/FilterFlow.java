package nine.collection;

import nine.function.Condition;

public class FilterFlow<T> implements Flow<T>
{
    Flow<T> source;
    Condition<T> condition;

    public FilterFlow(Flow<T> source, Condition<T> condition)
    {
        this.source = source;
        this.condition = condition;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        source.read(item ->
        {
            if(condition.match(item)) action.call(item);
        });
    }
}