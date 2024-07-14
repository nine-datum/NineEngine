package nine.geometry;

import nine.opengl.Drawing;

public interface MaterializedDrawing
{
	Drawing materialize(MaterialProvider materials);
}