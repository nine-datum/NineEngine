package nine.opengl;

public interface Profiler
{
    public interface Action { void call(); };
    public interface Function<T> { T call(); }

    void profile(String name, Action action);
    <T> T profile(String name, Function<T> action);
    
    static final Profiler none = new Profiler()
	{
		@Override
		public void profile(String name, Action action) {
			action.call();
		}

		@Override
		public <T> T profile(String name, Function<T> action) {
			return action.call();
		}
	};
}