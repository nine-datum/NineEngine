package nine.drawing;

import java.util.Arrays;

public interface Color
{
    void accept(ColorIntsAcceptor acceptor);
    
    static Color parse(String text)
    {
    	var floats = Arrays.stream(text.split(" ")).map(Float::parseFloat).toArray(Float[]::new);
    	return new ColorFloatStruct(floats[0], floats[1], floats[2], floats[3]);
    }
}
