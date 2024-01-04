package nine.main;

import nine.geometry.collada.StringReader;
import nine.math.FloatFunc;

public class FPSCounter
{
	int frames;
    float lastResetTime;
    FloatFunc time;
    StringReader reader;

	public FPSCounter(FloatFunc time, StringReader reader)
    {
        this.time = time;
        this.reader = reader;
    }

    public void frame()
	{
        var t = time.value();
        if(t - lastResetTime > 1f)
        {
            reader.read(String.valueOf(frames));
            frames = 0;
            lastResetTime = t;
        }
        frames++;
	}
}