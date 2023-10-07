package nine.input;

import nine.math.Vector2f;

public class WASD_Vector2f implements Vector2f
{
    Vector2f vector;
    
    public WASD_Vector2f(Keyboard keyboard)
    {
        Key w = keyboard.keyOf('w');
        Key a = keyboard.keyOf('a');
        Key s = keyboard.keyOf('s');
        Key d = keyboard.keyOf('d');
        vector = action ->
        {
            float x = 0f;
            float y = 0f;
            if(w.isDown()) y++;
            if(s.isDown()) y--;
            if(d.isDown()) x++;
            if(a.isDown()) x--;
            action.call(x, y);
        };
    }

    @Override
    public void accept(XYAction acceptor)
    {
        vector.accept(acceptor);
    }
}