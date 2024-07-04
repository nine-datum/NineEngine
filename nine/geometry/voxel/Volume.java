package nine.geometry.voxel;

import java.util.ArrayList;
import java.util.List;

import nine.math.Area;
import nine.math.Vector3f;

public interface Volume {
    boolean active(Vector3f point);

    static Volume sphere(Vector3f center, float radius) {
        return p -> {
            float dist = p.sub(center).length();
            return dist <= radius;
        };
    }
    static Volume cube(Vector3f center, Vector3f size) {
        Area area = Area.minmax(center, size);
        return p -> {
            return area.contains(p);
        };
    }

    static final int[][] sideNormals = {
        { -1, 0, 0 }, // left
        { 1, 0, 0 }, // right
        { 0, -1, 0 }, // bottom
        { 0, 1, 0 }, // top
        { 0, 0, -1 }, // back
        { 0, 0, 1 }, // front
    };

    default List<Vector3f> geometry(int sectionsX, int sectionsY, int sectionsZ) {
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        for(int x = 0; x < sectionsX; x++)
        for(int y = 0; y < sectionsY; y++)
        for(int z = 0; z < sectionsZ; z++)
        for(int[] normal : sideNormals) {

        }
        return vertices;
    }
}