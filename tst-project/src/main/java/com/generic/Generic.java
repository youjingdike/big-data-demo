package com.generic;

import java.util.List;

public class Generic {

	//方法一  
	public static <T> void get(List<? extends T> list)  {  
	    list.get(0);
	}
	
	//方法四  
	public static void set(List<? super B> list, B b)  
	{  
	    list.add(b);  
	} 

}