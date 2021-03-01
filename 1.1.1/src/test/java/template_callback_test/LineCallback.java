package template_callback_test;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
