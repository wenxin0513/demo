package com.example.basics.proxy.Test;

import com.example.basics.proxy.impl.Cinema;
import com.example.basics.proxy.impl.GuitaiA;
import com.example.basics.proxy.impl.MaotaiJiu;
import com.example.basics.proxy.impl.RealMovie;
import com.example.basics.proxy.interfa.Movie;
import com.example.basics.proxy.interfa.SellWine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyTest {

    public static void main(String[] args) {

        /**
         * 静态代理：
         * 1.由程序员创建由特定工具自动生成源代码，在对其编译，在程序进行之前，代理的.class文件就已经存在了
         * 2.静态代理通常只代理一个类
         * 3.静态代理事先知道代理的是哪个类
         */
//        RealMovie realMovie = new RealMovie();
//        Movie movie = new Cinema(realMovie);
//
//        movie.play();
        /**
         * 动态代理：
         * 1.在程序运行时通过反射机制实现
         * 2.动态代理通常代理多个类
         * 3.动态代理在程序还没有运行之前不知道代理的什么东西，只有运行时才知道
         */


        // TODO Auto-generated method stub

        MaotaiJiu maotaijiu = new MaotaiJiu();


        InvocationHandler jingxiao1 = new GuitaiA(maotaijiu);


        SellWine dynamicProxy = (SellWine) Proxy.newProxyInstance(MaotaiJiu.class.getClassLoader(),
                MaotaiJiu.class.getInterfaces(), jingxiao1);

        dynamicProxy.mainJiu();


        /**
         * 总结:
         * 代理分为静态代理和动态代理两种。
         * 静态代理，代理类需要自己编写代码写成。
         * 动态代理，代理类通过 Proxy.newInstance() 方法生成。
         * 不管是静态代理还是动态代理，代理与被代理者都要实现两样接口，它们的实质是面向接口编程。
         * 静态代理和动态代理的区别是在于要不要开发者自己定义 Proxy 类。
         * 动态代理通过 Proxy 动态生成 proxy class，但是它也指定了一个 InvocationHandler 的实现类。
         * 代理模式本质上的目的是为了增强现有代码的功能。
         */
    }

}
