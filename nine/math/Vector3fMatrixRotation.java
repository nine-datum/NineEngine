package nine.math;

public class Vector3fMatrixRotation implements Vector3f
{
    Matrix4f source;

    public Vector3fMatrixRotation(Matrix4f source)
    {
        this.source = source;
    }

    @Override
    public void accept(XYZAction acceptor)
    {
        /*double x, y, z;
			Matrix4x4 rotation = WithoutScale();
			double sy = Math.Sqrt(rotation[0, 0] * rotation[0, 0] + rotation[1, 0] * rotation[1, 0]);

			bool singular = sy < 1e-6;


			if (!singular)
			{
				x = Math.Atan2(rotation[2, 1], rotation[2, 2]);

				y = Math.Atan2(-rotation[2, 0], sy);

				z = Math.Atan2(rotation[1, 0], rotation[0, 0]);
			}
			else
			{
				x = Math.Atan2(-rotation[1, 2], rotation[1, 1]);

				y = Math.Atan2(-rotation[2, 0], sy);

				z = 0;
			}
			return new Vector3((float)x, (float)y, (float)z) * Mathf.Rad2Deg;*/
    }
}