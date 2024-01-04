package nine.geometry.procedural;

import java.util.ArrayList;

import nine.buffer.Buffer;
import nine.collection.Flow;
import nine.math.Matrix4f;
import nine.math.Vector2f;
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

    public interface GeometryBrush
    {
        GeometryBrush plane(Vector3f center, Vector3f rotation, Vector2f scale);
        Drawing drawing();
    }
    
    public static GeometryBrush brush(OpenGL gl)
    {
        class GL_Brush implements GeometryBrush
        {
            Flow<Vertex> vertices;

            GL_Brush(Flow<Vertex> vertices)
            {
                this.vertices = vertices;
            }

            @Override
            public GeometryBrush plane(Vector3f center, Vector3f rotation, Vector2f scale)
            {
                Matrix4f matrix = Matrix4f.transform(center, rotation, Vector3f.newXZ(scale));
                Vector3f normal = matrix.transformVector(Vector3f.newY(1f));

                Vertex bottomLeft = Vertex.of(
                    matrix.transformPoint(Vector3f.newXYZ(-0.5f, 0f, -0.5f)),
                    Vector2f.newXY(0, 0),
                    normal);
                Vertex bottomRight = Vertex.of(
                    matrix.transformPoint(Vector3f.newXYZ(0.5f, 0f, -0.5f)),
                    Vector2f.newXY(0, 0),
                    normal);
                Vertex topLeft = Vertex.of(
                    matrix.transformPoint(Vector3f.newXYZ(-0.5f, 0f, 0.5f)),
                    Vector2f.newXY(0, 0),
                    normal);
                Vertex topRight = Vertex.of(
                    matrix.transformPoint(Vector3f.newXYZ(0.5f, 0f, 0.5f)),
                    Vector2f.newXY(0, 0),
                    normal);

                return new GL_Brush(vertices.concat(Flow.of(
                    topLeft,
                    bottomLeft,
                    bottomRight,

                    bottomRight,
                    topRight,
                    topLeft
                )));
            }

            @Override
            public Drawing drawing()
            {
                return of(gl, vertices.collect());
            }
        }
        return new GL_Brush(Flow.of());
    }

    public static Drawing lineString(OpenGL gl, Vector2f tiling, Buffer<MaterialPoint> points)
    {
        if(points.length() == 1) return Drawing.empty();
        
        ArrayList<Vertex> vertices = new ArrayList<>();
        Buffer.range(1, points.length() - 1).flow().read(
        i -> points.at(i).accept(
        (width, point, normal) -> points.at(i - 1).accept(
        (lw, lp, ln) ->
        {
            Vector3f dir = lp.sub(point);
            Vector3f left = dir.normalized().cross(normal).mul(width * 0.5f);
            float uvY = i;
            var leftBottom = Vertex.of(lp.add(left), Vector2f.newXY(0f, uvY).mul(tiling), normal);
            var rightBottom = Vertex.of(lp.add(left.negative()), Vector2f.newXY(1f, uvY).mul(tiling), normal);

            var leftTop = Vertex.of(point.add(left), Vector2f.newXY(0f, uvY + 1f).mul(tiling), normal);
            var rightTop = Vertex.of(point.add(left.negative()), Vector2f.newXY(1f, uvY + 1f).mul(tiling), normal);
            
            vertices.add(rightTop);
            vertices.add(leftTop);
            vertices.add(leftBottom);

            vertices.add(leftBottom);
            vertices.add(rightBottom);
            vertices.add(rightTop);
        })));

        for(var v : vertices) v.accept((p, u, n) ->
        {
            p.accept((x, y, z) -> System.out.printf("position:%f, %f, %f\n", x, y, z));
            //u.accept((x, y) -> System.out.printf("uv:%f, %f\n", x, y));
            //n.accept((x, y, z) -> System.out.printf("normal:%f, %f, %f\n", x, y, z));
        });

        return of(gl, Buffer.of(vertices));
    }
}