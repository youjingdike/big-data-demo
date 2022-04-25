package com.java8;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LambdaTester {
	
	@Test
	public void testLambda() {
		//在最简单的形式中，一个lambda可以由用逗号分隔的参数列表、�C>符号与函数体三部分表示
		//在参数前面只能指定元素共同父类的类型
		Object obj = null;
		Arrays.asList( "a",obj,"c").forEach((e) -> System.out.println(e));
		//请注意参数e的类型是由编译器推测出来的。同时，你也可以通过把参数类型与参数包括在括号中的形式直接给出参数的类型
		Arrays.asList( "a",obj,"c").forEach((Object e) -> System.out.println(e));
		Arrays.asList( "a","b","c").forEach((String e) -> System.out.println(e));
		//如果方法体有多条语句的话，就放在{}里
		Arrays.asList( "a","b","c").forEach((String e) -> {
				System.out.println(e);
				System.out.println(e);
			});
		
	}
	
	@Test
	public void lambda1() {
		//Lambda可以引用类的成员变量与局部变量（如果这些变量不是final的话，它们会被隐含的转为final，这样效率更高）例如，下面两个代码片段是等价的：
		String separator = ",";
		Arrays.asList( "a", "b", "d" ).forEach( 
		    ( String e ) -> System.out.print( e + separator ) );
		final String separator1 = "@";
		Arrays.asList( "a", "b", "d" ).forEach( 
		    ( String e ) -> System.out.print( e + separator1 ) );
	}
	
	@Test
	public void lambda2() {
		//Lambda可能会返回一个值。返回值的类型也是由编译器推测出来的。如果lambda的函数体只有一行的话，那么没有必要显式使用return语句。下面两个代码片段是等价的：
		List<String> asList = Arrays.asList( "a", "d", "b" );
		asList.sort( ( e1, e2 ) -> e1.compareTo( e2 ) );
		asList.forEach(e -> System.out.println(e));
		
		List<String> asList1 = Arrays.asList( "a", "v", "nb" );
		asList1.sort( ( e1, e2 ) -> {
		    int result = e1.compareTo( e2 );
		    return result;
		} );
		asList1.forEach(e -> System.out.println(e));
	}
	
}

