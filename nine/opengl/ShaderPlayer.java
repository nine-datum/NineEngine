package nine.opengl;

import nine.function.FunctionSingle;

public interface ShaderPlayer
{
    Uniforms uniforms();
    Drawing play(Drawing drawing);
    void dispose();
    
    default ShaderPlayer wrapPlay(FunctionSingle<Drawing, Drawing> wrapper)
    {
    	var source = this;
    	return new ShaderPlayer() {

			@Override
			public Uniforms uniforms() {
				return source.uniforms();
			}

			@Override
			public Drawing play(Drawing drawing) {
				return source.play(wrapper.call(drawing));
			}

			@Override
			public void dispose() {
				source.dispose();
			}
    	};
    }
}