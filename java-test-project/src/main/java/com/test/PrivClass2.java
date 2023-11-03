package com.test;

public class PrivClass2 {
	private String name;
	
	public void comp(PrivClass2 ot) {
		ot.name = this.name;
	}
}
