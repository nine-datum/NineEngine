package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.math.Matrix4fLerp;
import nine.math.ValueFloat;
import nine.math.ValueFloatStruct;

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
    public Matrix4f animate(ValueFloat time)
    {
        if((frames.length() + keys.length()) == 0)
        {
            return Matrix4fIdentity.identity;
        }
        ValueFloatStruct lerp = new ValueFloatStruct(0f);
        Matrix4f[] ab = { Matrix4fIdentity.identity, Matrix4fIdentity.identity };
        Matrix4f matrix = new Matrix4fLerp(a -> ab[0].accept(a), a -> ab[1].accept(a), lerp);
        return a -> time.accept(t ->
        {
            int keysLength = keys.length();
            float last = keys.at(keysLength - 1);
            float frac = t - (last * (int)(t / last));
            
            int next = 1;
            int current = 0;
            float k = 0f;
            
            for(int i = 0; i < keysLength; i++)
            {
                k = keys.at(i);
                if(k > frac)
                {
                    current = i;
                    next = (i - 1);
                    if(next < 0) next = keysLength + next;
                    break;
                }
            }

            lerp.value = (k - frac) / (k - keys.at(next));
            ab[0] = frames.at(current);
            ab[1] = frames.at(next);
            matrix.accept(a);
        });
    }
}