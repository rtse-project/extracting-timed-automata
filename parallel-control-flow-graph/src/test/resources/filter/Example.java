public class Example {
    long _CONST_ = 10;

    public void example1(){
        Thread.sleep(50L);
        Thread.sleep(50 * _CONST_ * 1000);
        Thread.sleep(50 * _CONST_ * 1000L);
        Thread.sleep(50L + _CONST_ * 1000L);
        Thread.sleep(50L * _CONST_ * 1000L);
        Thread.sleep(50 / _CONST_ * 1000L);
        Thread.sleep(50L - _CONST_ + 1000L);
    }
}