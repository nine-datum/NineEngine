package nine.function;

public class ErrorPrinter implements ErrorHandler
{
    public static final ErrorPrinter instance = new ErrorPrinter();

    @Override
    public void call(Throwable error)
    {
        while(error != null)
        {
            error.printStackTrace(System.out);
            error = error.getCause();
        }
    }   
}
