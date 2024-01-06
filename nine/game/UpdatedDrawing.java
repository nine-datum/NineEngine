package nine.game;

import nine.function.Function;
import nine.geometry.collada.AnimatedSkeleton;
import nine.math.FloatFunc;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public interface UpdatedDrawing
{
    Drawing update(Matrix4f projection, Vector3f cameraPosition, Vector3f cameraRotation, Vector3f worldLight);

    static UpdatedDrawing ofModel(AnimatedDrawing model, AnimatedSkeleton animation, FloatFunc time, Function<Matrix4f> root)
    {
        return (projection, cameraPosition, cameraRotation, worldLight) -> model.animate(
            projection,
            worldLight,
            Matrix4f.transform(cameraPosition.negative(), cameraRotation.negative()).mul(root.call()),
            animation.animate(time.value()));
    }
}