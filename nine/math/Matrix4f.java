package nine.math;

public interface Matrix4f
{
    /*
        elements layout :

        [ 0  4  8  12 ] -- line 0
        [ 1  5  9  13 ] -- line 1
        [ 2  6  10 14 ] -- line 2
        [ 3  7  11 15 ] -- line 3

        [ 0  1  2  3  ] -- column 0
        [ 4  5  6  7  ] -- column 1
        [ 8  9  10 11 ] -- column 2
        [ 12 13 14 15 ] -- column 2
    */
    
    void accept(ElementsAcceptor acceptor);
}