package pointcutTest;

public class Target implements TargetInterface {
    @Override
    public void hello() { }

    @Override
    public void hello(String a) { }

    @Override
    public int minus(int a, int b) { return 0; }

    @Override
    public int plus(int a, int b) { return 0; }
    public void method() {}
}
interface TargetInterface {
    public void hello();
    public void hello(String a);
    public int minus(int a, int b);
    public int plus(int a, int b);
}
