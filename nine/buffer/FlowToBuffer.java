package nine.buffer;

import java.util.ArrayList;

import nine.collection.Flow;

public class FlowToBuffer<T> implements Buffer<T>
{
    ArrayList<T> list = new ArrayList<T>();

    public FlowToBuffer(Flow<T> flow)
    {
        flow.read(list::add);
    }

    @Override
    public T at(int index)
    {
        return list.get(index);
    }

    @Override
    public int length()
    {
        return list.size();
    }
}