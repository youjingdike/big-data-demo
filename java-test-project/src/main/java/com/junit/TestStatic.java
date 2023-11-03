package com.junit;

public class TestStatic {
    public static String str = "0";
    static String str1 = "1";
    private static String str2 = "2";
    
    public static void main(String[] args) {
        TestStatic static1 = new TestStatic();
        System.out.println(TestStatic.str);
        System.out.println(TestStatic.str1);
        System.out.println(TestStatic.str2);
        System.out.println("~~~~~~~~~~~~~~~~~~~");
        System.out.println(static1.str);
        System.out.println(static1.str1);
        System.out.println(static1.str2);
    }
}
