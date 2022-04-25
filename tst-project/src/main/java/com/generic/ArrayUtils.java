package com.generic;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayUtils {
	
	/**
	 * 把一个变长参数转变为列表，并且长度可变
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> List<T> asList(T...t) {
		List<T> list = new ArrayList<T>();
		Collections.addAll(list, t);
		return list;
	}
	
	/**
	 * 读操作要指定上限
	 * @param list
	 */
	public static <E> void read(List<? extends E> list) {
		System.out.println("**************");
		for (E e:list) {//读操作泛型类型必须是E的子类才能转换成E,否则没法转换
			System.out.println(e);
		}
	}
	
	/**
	 * 写操作要指定下限
	 * @param list
	 */
	public static void write(List<? super BigDecimal> list) {
		list.add(new BigDecimal("123"));//要添加BigDecimal类型,泛型类型必须是它的父类才能添加
		read(list);
	}
	
	/*public static void read(String[] listArr) {
		for (String e:listArr) {//读操作泛型类型必须是E的子类才能转换成E,否则没法转换
			System.out.println(e);
		}
	}*/
	
	/**
	 * 循环数组
	 * @param arr
	 */
	public static <E> void read(E[] arr) {
		for (E e:arr) {
			System.out.println(e+":read method");
		}
	}
	
	/**
	 * 将list转换成相应 <i><b>泛型</b></i> 的数组
	 * @param list
	 * @param tClass
	 * @return
	 */
	public static <T> T[] toArrays(List<T> list,Class<T> tClass) {
		if (list == null || tClass == null) {
			return null;
		}
		T[] t = (T[])Array.newInstance(tClass, list.size());
		for (int i=0,len=list.size();i<len;i++) {
			t[i] = list.get(i);
		}
		return t;
		
	}
	
	public static void testList(List<Integer> list) {
		
	}
}
