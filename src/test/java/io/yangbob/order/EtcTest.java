package io.yangbob.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EtcTest {

    @DisplayName("test test")
    @Test
    void test() {
        ClassA a1 = new ClassA();
        ClassA a2 = new ClassA();
        ClassB b = new ClassB();

        System.out.println("a1 == a2 ? " + (a1.getClass().equals(b.getClass())));
    }

    static class ClassA {
    }

    static class ClassB {
    }
}
