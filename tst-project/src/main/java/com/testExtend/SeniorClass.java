package com.testExtend;

public class SeniorClass {
    public SeniorClass(){
        System.out.println(toString()); //may throw NullPointerException if overridden
                                        //会调子类重写后的toString方法,尤其是在多次重写父类方法的时候要注意
                                        //new哪个子类就调用哪个子类的toString方法
    }
    public String toString(){
      return "IAmSeniorClass";
    }
    
}
