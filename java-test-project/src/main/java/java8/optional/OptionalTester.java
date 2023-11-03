package java8.optional;

import java.util.Optional;
/**
 * Optional：
 * 1.map()可级联调用
 * 2.尽量使用orElse(),orElseGet(),不要使用get(),如果要使用get(),必须先调用isPresent();
 * 3.Optional 不要用作属性或是方法参数
 * 4.Optional 主要用作返回类型
 * @author xq
 * @date 2018年6月30日
 */
public class OptionalTester {
	public static void main(String[] args) {
		test1();
//		test2();
	}
	
	private static void test1() {
//		Optional< String > fullName = Optional.ofNullable( null );
		Optional< String > fullName = Optional.ofNullable( "testestset" );
		System.out.println( "Full Name is set? " + fullName.isPresent() );        
		System.out.println( "Full Name: " + fullName.orElseGet( () -> "[none]" ) ); 
		System.out.println( fullName.map( s -> "Hey " + s + "!" ).orElse( "Hey Stranger!" ) );
	}
	
	private static void test2() {
		Optional< String > firstName = Optional.of( "Tom" );
		System.out.println( "First Name is set? " + firstName.isPresent() );        
		System.out.println( "First Name: " + firstName.orElseGet( () -> "[none]" ) ); 
		System.out.println( firstName.map( s -> "Hey " + s + "!" ).orElse( "Hey Stranger!" ) );
		System.out.println();
	}
}
