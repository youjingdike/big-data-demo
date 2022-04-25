package com.generic;

import java.util.ArrayList;
import java.util.List;

public class Generic2 {

	public static void main(String[] args) {
		List<? extends A> list1 = new ArrayList<A>();  
		List<? extends A> list2 = new ArrayList<B>();  
		List<? super B> list3 = new ArrayList<B>();  
		list3.add(new B());
		List<? super B> list4 = new ArrayList<A>();  
		list4.add(new B());
	}

}
