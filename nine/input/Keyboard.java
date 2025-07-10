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
    Key tab();
    Key arrowLeft();
    Key arrowRight();
    Key arrowUp();
    Key arrowDown();

    static final Keyboard empty = new Keyboard () {
        public Key keyOf(char symbol) { return Key.empty; }
        public void update() { }
        public Key backspace(){ return Key.empty; }
        public Key escape(){ return Key.empty; }
        public Key leftShift(){ return Key.empty; }
        public Key leftAlt(){ return Key.empty; }
        public Key leftCtrl(){ return Key.empty; }
        public Key space(){ return Key.empty; }
        public Key tab(){ return Key.empty; }
        public Key arrowLeft(){ return Key.empty; }
        public Key arrowRight(){ return Key.empty; }
        public Key arrowUp(){ return Key.empty; }
        public Key arrowDown(){ return Key.empty; }
    };
}
