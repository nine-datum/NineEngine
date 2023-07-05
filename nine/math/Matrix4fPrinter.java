package nine.math;

import nine.function.ActionSingle;

public class Matrix4fPrinter
{
    ActionSingle<String> printer;

    public Matrix4fPrinter(ActionSingle<String> printer)
    {
        this.printer = printer;
    }

    public void print(Matrix4f matrix)
    {
        matrix.accept(elements ->
        {
            printer.call("[");
            for(int i = 0; i < 16; i++)
            {
                if(i != 0) printer.call(", ");
                printer.call(String.valueOf(elements.at(i)));
            } 
            printer.call("]");
        });
    }
}