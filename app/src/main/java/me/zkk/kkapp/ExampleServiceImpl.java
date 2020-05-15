package me.zkk.kkapp;

public class ExampleServiceImpl implements ExampleService {

    @Override
    public String sayHello() {
        return "Server says HelloWorld";
    }

    public int computeSum(int a, int b) {
        int sum = a;
        for (int i = a; i < b; i++) {
            sum += i;
        }
        return sum;
    }

    public String replace(TestClass test) {
        return test.getA();
    }

    public String returnParam(String str) {
        return str;
    }

    @Override
    public long count(long start, long end) {
        long i, j;
        for(i = start; i <= end; ++i) {
            for(j = start; j <= end; ++j);
        }
        return i;
    }

}
