package com.eclipseUsed;

/**
 * 
 * @author xingqian
 *
 */
public class ErrorTest {
    private static int test = 0;
    private static String str = "";
    
    public String trim() {
        return str.trim();
    }
    public static void main(String[] args) {
        System.out.println(factorial(6));
    }
   public static int factorial(int value) {
       test++;
       if (value == 1) {
           return value;
       } else {
           return value * factorial(--value);
       }
   }
   public static int getTest() {
    return test;
   }
    public static void setTest(int test) {
        ErrorTest.test = test;
    }
    public static String getStr() {
        return str;
    }
    public static void setStr(String str) {
        ErrorTest.str = str;
    }
    public void testMethod() {
           System.out.println(str);
   }
}
