package java8.consumer;

import java.util.function.Consumer;

public class Test01 {
	public static void main(String[] args) {  
        Consumer<String> c = (x) -> System.out.println(x.toLowerCase());  
        c.accept("CONSUMER");  
    }
}
