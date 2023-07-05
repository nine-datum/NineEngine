package nine.buffer;

public class SplitTextBuffer implements Buffer<String>
{
    String[] split;

    public SplitTextBuffer(String text, String regex)
    {
        split = text.split(regex);
    }

    @Override
    public String at(int i) { return split[i]; }
    @Override
    public int length() { return split.length; }
}