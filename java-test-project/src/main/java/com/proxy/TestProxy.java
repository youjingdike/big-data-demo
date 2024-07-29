package com.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestProxy {
    public static void main(String[] args) {
        InvokedProxy invokedProxy = new InvokedProxy();

        // TODO 在创建代理类时，传入Proxy.newProxyInstance的第二个参数Class<?>[] interfaces时，
        //  传入的接口会影响isInstance的判断，如果传入的接口中包含Child1.class，
        //  那么下面的Child1.class.isInstance(childProxy)返回true(即使childProxy是Child的引用)，否则返回false
        //  为什么会这样呢？
        //  因为创建的代理对象会实现所有传入的interfaces接口，也就是说通过Proxy.newProxyInstance创建的代理对象，可以类型转换成interfaces中的任何一种。
        Child childProxy = (Child) Proxy.newProxyInstance(TestProxy.class.getClassLoader(), new Class[]{Child.class, Parent.class, ChildA.class}, invokedProxy);
        childProxy.start();
        childProxy.stop();

        System.out.println(ChildA.class.isInstance(childProxy));
        if (ChildA.class.isInstance(childProxy)) {
            ChildA childAProxy = (ChildA) childProxy;
            childAProxy.haha();
        }
        System.out.println("@@@@@@@@@@@@@@");
        ChildA childAProxy = (ChildA) Proxy.newProxyInstance(TestProxy.class.getClassLoader(), new Class[]{Child.class, Parent.class, ChildA.class}, invokedProxy);
        childAProxy.haha();
        System.out.println(Child.class.isInstance(childAProxy));

        System.out.println("@@@@@@@@@@@@@@");
        for (Class<?> anInterface : Impl.class.getInterfaces()) {
            System.out.println("@:"+anInterface);
            System.out.println(Parent.class.isAssignableFrom(anInterface));
        }
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
        Class<?> declaringClass = method.getDeclaringClass();
        System.out.println(declaringClass);
        Object result = null;
        if (declaringClass == ChildA.class) {
            rpc(method);
        } else {
            result = method.invoke(this, args);
        }
        return result;
    }

    private void rpc(Method method) {
        System.out.println(method.getDeclaringClass()+"的方法需要调用rpc的方法");
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

interface ChildA extends Parent {
    // TODO 如果这里重新实现了start(),在使用代理调用start()时，invoke方法中获取start()方法的申明类时，返回Child,
    //  如果这里不定义start(),就返回的Parent
    void start();
    void stop();
    void haha();
}

interface A {}
interface B {}

class Impl implements ChildA,Child,A,B {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void haha() {

    }
}