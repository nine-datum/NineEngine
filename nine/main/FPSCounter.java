package nine.main;

import nine.geometry.collada.StringReader;
import nine.math.ValueFloat;

public class FPSCounter
{
	int frames;
    float lastResetTime;
    ValueFloat time;
    StringReader reader;

	public FPSCounter(ValueFloat time, StringReader reader)
    {
        this.time = time;
        this.reader = reader;
    }

    public void frame()
	{
        time.accept(t ->
        {
            if(t - lastResetTime > 1f)
            {
                reader.read(String.valueOf(frames));
                frames = 0;
                lastResetTime = t;
            }
        });
        frames++;
	}
}