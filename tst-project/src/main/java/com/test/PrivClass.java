package com.test;

public class PrivClass {
	private String name;
	
	/**
	 * private修饰的，可以在相同对象的方法里使用
	 * @param ot
	 */
	public void comp(PrivClass ot) {
		ot.name = this.name;
	}
	
}
