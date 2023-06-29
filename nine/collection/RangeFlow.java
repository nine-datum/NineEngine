package nine.collection;

public class RangeFlow implements Flow<Integer>
{
    int start;
    int end;
    
    public RangeFlow(int length)
    {
        this.end = length;
    }

    public RangeFlow(int start, int length)
    {
        this.start = start;
        this.end = start + length;
    }

    @Override
    public void read(FlowAction<Integer> action)
    {
        for(int i = start; i < end; i++) action.call(i);
    }
}