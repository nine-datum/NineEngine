package nine.input;

public interface Keyboard
{
    Key keyOf(char symbol);
    void update();

    Key backspace();
    Key escape();
    Key leftShift();
    Key leftAlt();
    Key leftCtrl();
    Key space();
}