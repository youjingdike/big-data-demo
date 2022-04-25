package java8.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Test03 {
	public static void main(String[] args) {  
        List<Student> employees = Arrays.asList(  
                new Student(1, 5, "John"),  
                new Student(2, 4, "Jane"),  
                new Student(3, 3, "Jack")  
        );  
  
        // with predicate  
        System.out.println(findStudents(employees, createCustomPredicateWith(4)));  
  
        // with function definition, both are same  
        Function<Double, Predicate<Student>> customFunction = threshold -> (e -> e.gpa > threshold);  
        System.out.println(findStudents(employees, customFunction.apply(4D)));  
    }  
  
    private static Predicate<Student> createCustomPredicateWith(double threshold) {  
        return e -> e.gpa >= threshold;  
    }  
  
    private static List<Student> findStudents(List<Student> employees, Predicate<Student> predicate) {  
        List<Student> result = new ArrayList<>();  
  
        for (Student e : employees) {  
            if (predicate.test(e)) {  
                result.add(e);  
            }  
        }  
  
        return result;  
    }  
}

class Student {  
    public int id;  
    public long gpa;  
    public String name;  
  
    Student(int id, long g, String name) {  
        this.id = id;  
        this.gpa = g;  
        this.name = name;  
    }  
  
    @Override  
    public String toString() {  
        return id + ":" + name + ": " + gpa;  
    }  
} 
