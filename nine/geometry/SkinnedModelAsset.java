package nine.geometry;

import nine.io.Storage;
import nine.opengl.OpenGL;

public interface SkinnedModelAsset
{
    SkinnedModel load(OpenGL gl, Storage storage);
}