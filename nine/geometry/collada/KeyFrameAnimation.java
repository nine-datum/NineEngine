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
        ValueFloatStruct lerp = new ValueFloatStruct(0f);
        Matrix4f[] ab = { Matrix4fIdentity.identity, Matrix4fIdentity.identity };
        Matrix4f matrix = new Matrix4fLerp(a -> ab[0].accept(a), a -> ab[1].accept(a), lerp);
        return a -> time.accept(t ->
        {
            int keysLength = keys.length();
            float last = keys.at(keysLength - 1);
            float frac = t - (last * (int)(t / last));
            
            int start = 0;
            int end = 1;
            float k = 0f;
            
            for(int i = 0; i < keysLength; i++)
            {
                k = keys.at(i);
                if(k > frac)
                {
                    end = i;
                    start = (i - 1);
                    if(start < 0) start = keysLength + start;
                    break;
                }
            }

            lerp.value = (frac - k) / (keys.at(end) - k);
            ab[0] = frames.at(start);
            ab[1] = frames.at(end);
            ab[0].accept(a);
        });
    }
}