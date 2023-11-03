package com.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class Test1 {
	
	public static  <T extends Comparable<T>> void mySort1(List<T> list) {
		Collections.sort(list);
	}
	
	public static  <T extends Comparable<? super T>> void mySort2(List<T> list) {
		Collections.sort(list);
	}
	
	public static void main(String[] args) {
		Demo<ArrayList> p = null;//编译正确
//		Demo<Collection> c = null;//报错,限制了上限
//		Demo1<GregorianCalendar> p1 = null;//编译报错,因为GregorianCalendar没有实现Comparable<GregorianCalendar>,只是继承了Comparable<Calendar>
		Demo2<GregorianCalendar> p2 = null;//编译正确,因为GregorianCalendar继承了Comparable<Calendar>，Calendar是GregorianCalendar的父类
		
		List<Animal> animals = new ArrayList<Animal>();
		animals.add(new Animal(12));
		animals.add(new Dog(11));
		animals.add(new Dog(1));
		animals.add(new Dog(111));
		
		List<Dog> dogs = new ArrayList<Dog>();
		dogs.add(new Dog(121));
		dogs.add(new Dog(12));
		dogs.add(new Dog(122));
		dogs.add(new Dog(1222));
		
		mySort1(animals);
		ArrayUtils.read(animals);
//		mySort1(dogs);//报错,限制了上限
		
		mySort2(animals);
		ArrayUtils.read(animals);
		mySort2(dogs);
		ArrayUtils.read(dogs);
	}
}

class Demo<T extends List>{
	
}

class Demo1<T extends Comparable<T>>{
	
}

class Demo2<T extends Comparable<? super T>>{
	
}

class Animal implements Comparable<Animal> {
	protected int age;
	
	public Animal(int age) {
		this.age = age;
	}

	public int compareTo(Animal o) {
		return this.age - o.age;
	}
	
	public String toString() {
		return this.age+"";
	}
}

class Dog extends Animal {

	public Dog(int age) {
		super(age);
	}
	
}