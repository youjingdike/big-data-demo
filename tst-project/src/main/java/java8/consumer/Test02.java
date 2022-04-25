package java8.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Test02 {
	public static void main(String[] args) {  
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  
  
        //使用匿名函数形式  
        numbers.forEach(new Consumer<Integer>() {  
            @Override  
            public void accept(Integer integer) {  
                System.out.println(integer);  
            }  
        });  
  
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //使用Lambda  
        List<Integer> numbers2 = Arrays.asList(11, 21, 31, 41, 51, 61, 71, 81, 91, 110);  
        
        Consumer<Integer> consumer1=(x)-> System.out.println(x);  
        numbers2.forEach(consumer1);
        
        Consumer<List> consumer=(x)-> System.out.println(x);  
        consumer.accept(numbers2);  
    } 
}
