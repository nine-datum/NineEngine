package nine.collection;

public class SkipEmptyTextFlow implements Flow<String>
{
    Flow<String> source;

    public SkipEmptyTextFlow(Flow<String> source)
    {
        this.source = source;
    }

    @Override
    public void read(FlowAction<String> action)
    {
        source.read(s ->
        {
            if(s.length() != 0) action.call(s);
        });
    }
}