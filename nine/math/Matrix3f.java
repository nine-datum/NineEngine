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
    float at(int index);

    public static float det(Elements s)
    {
        return s.at(0) * s.at(4) * s.at(8) - 
            s.at(0) * s.at(5) * s.at(7) -
            s.at(1) * s.at(3) * s.at(8) +
            s.at(2) * s.at(3) * s.at(7) +
            s.at(1) * s.at(5) * s.at(6) -
            s.at(2) * s.at(4) * s.at(6);
    }
}