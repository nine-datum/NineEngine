package nine.geometry;

import nine.buffer.Buffer;
import nine.math.Matrix4f;

public class KeyFrameAnimation implements Animation
{
    Buffer<Float> keys;
    Buffer<Matrix4f> frames;
    
    public KeyFrameAnimation(Buffer<Float> keys, Buffer<Matrix4f> frames)
    {
        this.keys = keys;
        this.frames = frames;
    }

    @Override
    public Matrix4f animate(float time)
    {
        if((frames.length() + keys.length()) == 0)
        {
            return Matrix4f.identity;
        }
        float lerp = 0;
        Matrix4f a;
        Matrix4f b;

        int keysLength = keys.length();
        
        if(keysLength == 1) return frames.at(0);
        
        float last = keys.at(keysLength - 1);
        float frac = time - (last * (int)(time / last));
        
        float dist = keys.at(1) - keys.at(0);
        
        int current = (int)(frac / dist);
        int next = current + 1;
        if(next >= keysLength) next = 0;
        
        float k = keys.at(current);

        lerp = (frac - k) / dist;
        a = frames.at(current);
        b = frames.at(next);
        
        return a.lerp(b, lerp);
    }
}