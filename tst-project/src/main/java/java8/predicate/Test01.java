package java8.predicate;

import java.util.function.Predicate;

public class Test01 {
	public static void main(String[] args) {  
        Predicate<String> predicate=(s)->s.length()>5;  
        System.out.println(predicate.test("pre------dicate"));  
        System.out.println(predicate.test("pre-"));  
    }  
}
