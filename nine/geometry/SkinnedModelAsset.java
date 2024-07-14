package nine.geometry;

import nine.opengl.OpenGL;

public interface SkinnedModelAsset
{
    ShadedSkinnedModel load(OpenGL gl);
}