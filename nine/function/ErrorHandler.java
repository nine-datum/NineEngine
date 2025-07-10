package nine.function;

public interface ErrorHandler
{
    void call(Throwable error);

    static final ErrorHandler rethrow = error -> { throw new RuntimeException(error); };
}
