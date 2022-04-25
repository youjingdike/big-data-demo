package com.vo;

public class Person {
	public String name; 
	private int psw; 
	private int age;
	
	public Person() {
	}

	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPsw() {
		return psw;
	}

	public void setPsw(int psw) {
		this.psw = psw;
	}

//	public int getAge() {
//		return age;
//	}
//
//	public void setAge(int age) {
//		this.age = age;
//	}
	
}
