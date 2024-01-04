package nine.geometry;

import nine.io.Storage;
import nine.opengl.OpenGL;

public interface SkinnedModelAsset
{
    ShadedSkinnedModel load(OpenGL gl, Storage storage);
}