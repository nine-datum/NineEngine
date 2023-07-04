package nine.collection;

import java.util.ArrayList;
import java.util.List;

public class CachedFlow<T> implements Flow<T>
{
    List<T> list;
    
    public CachedFlow(Flow<T> flow)
    {
        list = new ArrayList<T>();
        flow.read(list::add);
    }

    @Override
    public void read(FlowAction<T> action)
    {
        for(T item : list) action.call(item);
    }
}