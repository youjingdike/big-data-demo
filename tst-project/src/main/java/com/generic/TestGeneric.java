package com.generic;

import java.math.BigDecimal;
import java.util.List;

public class TestGeneric<T> {
    
    public void callMe(T t) {
        System.out.println(t);
    }
    
    public void callMe1(List list) {
        System.out.println(list);
    }
    
    public static void getData(Box<?> data) {
    	System.out.println("data :" + data.getData());
    }
    public static void getUpperNumberData(Box<? extends Number> data){
        System.out.println("data :" + data.getData());
    }
    public static void getLowerNumberData(Box<? super BigDecimal> data){
        System.out.println("data :" + data.getData());
    }
    
    public static  <E> void getLowerNumberDataT(Box<? super E> data){
        System.out.println("data :" + data.getData());
    }
    
    public static void main(String[] args) {
//        TestGeneric<Object> t = new TestGeneric<Object>();
        /*TestGeneric<String> t = new TestGeneric<String>();
        List list = new ArrayList();
        list.add("s");
        list.add("s");
        list.add("s");
        list.add("s");
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        t.callMe("sss");
        t.callMe1(list);*/
        
        Box<String> name = new Box<String>("corn");
        Box<Integer> age = new Box<Integer>(712);
        Box<Number> number = new Box<Number>(314);
        Box<BigDecimal> numberBigDecimal = new Box<BigDecimal>(new BigDecimal("123456"));
         
        getData(name);
        getData(age);
        getData(number);
        
//        getUpperNumberData(name);
        getUpperNumberData(age);
        getUpperNumberData(number);
        
        getLowerNumberData(number);
        getLowerNumberData(numberBigDecimal);
        
        getLowerNumberDataT(name);
        getLowerNumberDataT(numberBigDecimal);
    }
}

class Box<T> {
 
     private T data;

     public Box() {
 
     }
 
     public Box(T data) {
         setData(data);
     } 
     public T getData() {
         return data;
     }
 
     public void setData(T data) {
         this.data = data;
     }
 
 }
