package com.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestProxy {
    public static void main(String[] args) {
        InvokedProxy invokedProxy = new InvokedProxy();
        Child testInterface = (Child) Proxy.newProxyInstance(TestProxy.class.getClassLoader(), new Class[]{Child.class}, invokedProxy);
        testInterface.start();
        testInterface.stop();
    }

}

class InvokedProxy implements InvocationHandler, Child {

    @Override
    public void stop() {
        System.out.println("stop");
    }

    @Override
    public void start() {
        System.out.println("start");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getDeclaringClass());
        Object result;
        result = method.invoke(this, args);
        return result;
    }
}

interface Parent{
    void start();
}

interface Child extends Parent {
    // TODO 如果这里重新实现了start(),在使用代理调用start()时，invoke方法中获取start()方法的申明类时，返回Child,
    //  如果这里不定义start(),就返回的Parent
    void start();
    void stop();
}