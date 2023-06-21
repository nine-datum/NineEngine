package nine.opengl;

import nine.collection.ArrayFlow;
import nine.collection.Flow;

public class CompositeDrawing implements Drawing
{
    Flow<Drawing> drawings;

    public CompositeDrawing(Drawing... drawings)
    {
        this.drawings = new ArrayFlow<Drawing>(drawings);
    }
    public CompositeDrawing(Flow<Drawing> drawings)
    {
        this.drawings = drawings;
    }

    @Override
    public void draw()
    {
        drawings.read(d -> d.draw());
    }
}