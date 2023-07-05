package nine.buffer;

import nine.collection.BufferToFlow;
import nine.collection.SkipEmptyTextFlow;

public class TextElementsBuffer implements Buffer<String>
{
    Buffer<String> buffer;

    public TextElementsBuffer(String text)
    {
        buffer = new FlowToBuffer<>(
            new SkipEmptyTextFlow(
                new BufferToFlow<>(
                    new SplitTextBuffer(text, "\\s"))));
    }

    @Override
    public String at(int i)
    {
        return buffer.at(i);
    }

    @Override
    public int length()
    {
        return buffer.length();
    }
}