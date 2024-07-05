package nine.opengl;

import java.util.List;

public interface Uniform<T>
{
    void load(T value);
    
    @SafeVarargs
    static<T> Uniform<T> many(Uniform<T>...uniforms)
    {
    	return many(List.of(uniforms));
    }
    static<T> Uniform<T> many(Iterable<Uniform<T>> uniforms)
    {
    	return item ->
    	{
    		for(var u : uniforms) u.load(item);
    	};
    }
}