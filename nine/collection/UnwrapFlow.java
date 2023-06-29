package nine.collection;

import nine.buffer.Buffer;

public class UnwrapFlow implements Flow<Float>
{
    Buffer<Float> data;
    Flow<Integer> indices;
    int size;

    public UnwrapFlow(Buffer<Float> data, Buffer<Integer> indices, int size)
    {
        this.data = data;
        this.indices = new BufferToFlow<Integer>(indices);
        this.size = size;
    }

    @Override
    public void read(FlowAction<Float> action)
    {
        indices.read(i ->
        {
            for(int s = 0; s < size; s++) action.call(data.at(i * size + s));
        });
    }
}