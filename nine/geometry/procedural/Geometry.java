package nine.geometry.procedural;

import java.util.ArrayList;

import nine.buffer.Buffer;
import nine.math.Vector3f;
import nine.opengl.Drawing;
import nine.opengl.OpenGL;

public class Geometry
{
    public static Drawing of(OpenGL gl, Buffer<Vertex> vertices)
    {
        ArrayList<Float> positions = new ArrayList<Float>();
        ArrayList<Float> uvs = new ArrayList<Float>();
        ArrayList<Float> normals = new ArrayList<Float>();
        vertices.flow().read(v -> v.accept((position, uv, normal) ->
        {
            position.accept((x, y, z) ->
            {
                positions.add(x);
                positions.add(y);
                positions.add(z);
            });
            uv.accept((x, y) ->
            {
                uvs.add(x);
                uvs.add(y);
            });
            normal.accept((x, y, z) ->
            {
                normals.add(x);
                normals.add(y);
                normals.add(z);
            });
        }));

        return gl.vao(Buffer.range(vertices.length()))
			.attribute(3, Buffer.of(positions))
			.attribute(2, Buffer.of(uvs))
            .attribute(3, Buffer.of(normals)).drawing();
    }

    public static Drawing lineString(OpenGL gl, Buffer<MaterialPoint> points)
    {
        if(points.length() == 1) return Drawing.empty();
        
        ArrayList<Vertex> vertices = new ArrayList<>();
        int length = points.length();

        for(int i = 1; i < length; i++)
        {
            points.at(i).accept((width, point, normal) ->
            {
                if(i == 1) points.at(i - 1).accept((lw, lp, ln) ->
                {
                    Vector3f dir = lp.sub(point);
                    Vector3f left = dir.normalized().cross(normal).mul(width);
                });
            });
        }
    }
}