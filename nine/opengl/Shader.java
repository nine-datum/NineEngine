package nine.opengl;

import nine.function.ActionSingle;

public interface Shader
{
    void play(ActionSingle<Uniforms> action);
}