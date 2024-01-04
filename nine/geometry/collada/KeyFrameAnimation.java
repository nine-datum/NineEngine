package nine.geometry.collada;

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
        Matrix4f a = Matrix4f.identity;
        Matrix4f b = Matrix4f.identity;

        int keysLength = keys.length() - 1;
        float last = keys.at(keysLength - 1);
        float frac = time - (last * (int)(time / last));
        
        int next = 1;
        int current = 0;
        float k = 0f;
        
        for(int i = keysLength - 1; i >= 0; i--)
        {
            k = keys.at(i);
            if(frac > k)
            {
                current = i;
                next = (i + 1) % keysLength;
                break;
            }
        }

        lerp = (frac - k) / (keys.at(1) - keys.at(0));
        a = frames.at(current);
        b = frames.at(next);
        
        return a.lerp(b, lerp);
    }
}