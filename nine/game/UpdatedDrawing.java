package nine.game;

import java.util.ArrayList;
import java.util.List;

import nine.function.Function;
import nine.geometry.collada.AnimatedSkeleton;
import nine.main.TransformedDrawing;
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
            projection.mul(Matrix4f.firstPersonCamera(cameraPosition, cameraRotation)),
            worldLight,
            root.call(),
            animation.animate(time.value()));
    }
    static UpdatedDrawing ofModel(TransformedDrawing model, Function<Matrix4f> root)
    {
        return (projection, cameraPosition, cameraRotation, worldLight) -> model.transform(
            projection.mul(Matrix4f.firstPersonCamera(cameraPosition, cameraRotation)),
            worldLight,
            root.call());
    }
    static UpdatedDrawing of(UpdatedDrawing... drawings)
    {
        return (proj, cPos, cRot, light) ->
        {
            List<Drawing> list = new ArrayList<>(drawings.length);
            for(var drawing : drawings) list.add(drawing.update(proj, cPos, cRot, light));
            return () ->
            {
                for(var drawing : list) drawing.draw();
            };
        };
    }
}