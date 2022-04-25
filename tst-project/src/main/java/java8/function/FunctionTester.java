package java8.function;

import java.util.function.Function;

public class FunctionTester {
	public static void main(String[] args) {  
        //简单的,只有一行  
        Function<String, String> function1 = (x) -> "result1: " + x;  
  
        //标准的,有花括号, return, 分号.  
        Function<String, String> function2 = (x) -> {  
            return "result2: " + x;  
        };  
  
        System.out.println(function1.apply("98")); 
        System.out.println();
        System.out.println(function1.andThen(function2).apply("100"));//先执行function1 然后将其结果作为参数传递到function2中  
        System.out.println();
        System.out.println(function2.compose(function1).apply("102"));//先执行function1 再执行function2  
        System.out.println();
        System.out.println(Function.identity());  
    }  
}
