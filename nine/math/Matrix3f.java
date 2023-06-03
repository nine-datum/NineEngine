package nine.math;

public interface Matrix3f
{
    /*
        elements layout :

        [ 0 3 6 ] -- line 0
        [ 1 4 7 ] -- line 1
        [ 2 5 8 ] -- line 2

        [ 0 1 2 ] -- column 0
        [ 3 4 5 ] -- column 1
        [ 6 7 8 ] -- column 2
    */
    void accept(ElementsAcceptor acceptor);
}