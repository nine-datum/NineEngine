package nine.opengl;

import nine.collection.ArrayFlow;
import nine.collection.Flow;

public class CompositeUniform implements Uniform
{
    Flow<Uniform> uniforms;

    public CompositeUniform(Uniform... uniforms)
    {
        this.uniforms = new ArrayFlow<Uniform>(uniforms);
    }
    public CompositeUniform(Flow<Uniform> uniforms)
    {
        this.uniforms = uniforms;
    }

    @Override
    public void load()
    {
        uniforms.read(d -> d.load());
    }
}