package nine.opengl;

public interface ShaderSourceAcceptor
{
    void call(CharSequence source);

    static final ShaderSourceAcceptor print = source -> System.out.println(source.toString());
}
