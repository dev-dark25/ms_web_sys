package tools;

public interface ILog {
    void debug(Object msg);

    void info(Object msg);

    void info();

    void warn(Object msg);

    void warn();

    void error();

    void error(Object msg);

    void error(Throwable t);

    void error(Object msg, Throwable t);
}
