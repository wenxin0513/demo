package com.example.basics.synchronizedtest;

public class synchronizedTest {
    public static void main(String[] args) throws InterruptedException {
       SynchronizedTest synchronizedTest = new SynchronizedTest();
       synchronizedTest.get();
       synchronizedTest.get1();
    }
    private static class SynchronizedTest {
        public void get() throws InterruptedException {
            synchronized (this) {
                System.out.println("小张你好鸭！");
                Thread.sleep(100000);
            }
        }
        public void get1() {
            synchronized (this) {
                System.out.println("小李你好鸭！");
            }
        }
    }

}
