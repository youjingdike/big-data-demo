package com.generic;

import java.math.BigDecimal;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		//正常使用,通过参数能确定泛型的类型,就不需要强制声明泛型类型
		List<String> list1= ArrayUtils.asList("A","B","C");
		List<BigDecimal> list4= ArrayUtils.asList(new BigDecimal("3333333"));
		
		//参数为空,想指定返回类型,就强制声明一下
		List<Integer> list2= ArrayUtils.<Integer>asList();
		List<Integer> list2_1= ArrayUtils.asList(12,12);
		List<Object> list2_2= ArrayUtils.asList();
		List list2_3= ArrayUtils.asList();
		//参数为整数和小数的混合,确定不了泛型的类型,只能强制声明
		List<Number> list3= ArrayUtils.<Number>asList(1,2,3.1);
		
		ArrayUtils.read(list1);
		ArrayUtils.read(list2);
		ArrayUtils.read(list3);
		ArrayUtils.read(list4);
		
		ArrayUtils.testList(list2);
		ArrayUtils.testList(list2_1);
		//ArrayUtils.testList(list2_2);
		ArrayUtils.testList(list2_3);
		
		//ArrayUtils.write(list3);
		//ArrayUtils.write(list4);
		
		//String[] strArr = (String[])list1.toArray();
		
		Object[] array = list1.toArray();
		/*for (Object o : array) {
			System.out.println(o+";;;;;;;;;;;");
		}*/
		ArrayUtils.read(array);
		
		Object[] obj = new Object[1];
		//会报错：java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;
		//String[] strArr = (String[])obj;
		
		String[] ss = new String[1];
		Object[] obj3 = ss;
		String[] strArr3 = (String[])obj3;
		
		Object[] obj1 = new String[1];
		String[] strArr1 = (String[])obj1;
		
		Object[] objArr = {"A","B"};
		//会报错：java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;
		//String[] s = (String[])objArr;
		
		String[] ss1 = {"A","B"};
		Object[] objArr1 = ss1;
		String[] s1 = (String[])objArr1;
		
		String[] arrays = ArrayUtils.toArrays(list1,String.class);
		ArrayUtils.read(arrays);
		
		BigDecimal[] bdArr = new BigDecimal[5];
		bdArr[0] = new BigDecimal("0");
		bdArr[1] = new BigDecimal("1");
		bdArr[2] = new BigDecimal("2");
		bdArr[3] = new BigDecimal("3");
		bdArr[4] = new BigDecimal("4");
		ArrayUtils.read(bdArr);
	}
}
