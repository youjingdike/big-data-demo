package com.reflect;

import com.vo.Person;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestReflection {
	
	/**
	 * 测试获取Class对象 
	 */
	private static void testGetClassObject(){
		//Object的getClass方法
//		Person p = new Person("高7");
//		Class  c = p.getClass();        //p  Persion类  的Class对象
////		System.out.println(c.getName());
////		System.out.println(c.getPackage());
////		//使用类名.class的方式
//		Class c2 = Person.class;
//		
//		System.out.println(c==c2);
//		System.out.println(c2.getName()); 
//		
//		//使用Class.forName()方式
		try {
			Class c3 = Class.forName("com.vo.Person");
			System.out.println(c3.getName());
			System.out.println(c3.getSimpleName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * 测试属性操作
	 */
	private static void testField(){
		try {
			Class c = Class.forName("com.vo.Person");
////			Field[] fs = c.getFields();     // 只能获得public的属性信息
////			Field[]  fs2=c.getDeclaredFields();  //获得所有声明的属性信息
////			for (int i = 0; i < fs2.length; i++) {
////				Field field = fs2[i];
////				System.out.println(field.getName());
////				System.out.println("属性类型："+field.getType().getName()); //属性是公有才行。 
////			}
//			//得到某个对象指定属性的值
			Field f = c.getField("name");   //Person类的 name属性的信息
			System.out.println(f.get(new Person("高7")));  
//
//			//得到类的静态属性
//			Field f2= c.getField("age");
//			System.out.println("类的静态属性值："+f2.get(c));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	/**
	 * 测试执行对象或类的方法
	 */
	private static void testMethod(){
		try {
			Class c = Class.forName("com.vo.Person");
			Method[] ms = c.getMethods();
			for (int i = 0; i < ms.length; i++) {
				Method method = ms[i];
				System.out.println(method.getName()); 
			}
//			//对象方法的调用
			Person p = new Person("高7");
			Method m = c.getMethod("test1",null);
			m.invoke(p, null);
//			//有参数传入的方法的调用!
			Method m2 = c.getMethod("test2",int.class,String.class);
			m2.invoke(p,22,"aaa");
//			
//			//类的静态方法调用
			Method m3 = c.getMethod("test3", int.class,String.class);
			m3.invoke(null, 33,"ddd");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	/**
	 * 测试构造新的对象
	 */
	private static void testConstructor(){
		try {
			Class c = Class.forName("com.vo.Person");
			Constructor con = c.getConstructor();
			Person p = (Person) con.newInstance();
			p.setName("ddd");
			System.out.println(p.getName());
						
//			Constructor con2 = c.getConstructor(String.class);
//			Person p2 = (Person) con2.newInstance("eee");
//			System.out.println(p2.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 测试某个对象是否为某个类的对象
	 */
	private static void testIsInstance(){
		Person p = new Person();
		Class c = TestReflection.class;
		Class c2 = Person.class;
		System.out.println(c.isInstance(p));
		System.out.println(c2.isInstance(p));
	}
	
	/**
	 * 测试某个对象是否为某个类的对象
	 * @throws SecurityException 
	 * @throws Exception 
	 */
	private static void testCast(Object obj) throws Exception{
		System.out.println(obj.getClass());
		Field f = obj.getClass().getDeclaredField("name"); //xxx是希望获取的属性
		f.setAccessible(true);
		Object value = f.get(obj);
		System.out.println(value);
	}
	public static void main(String[] args) {
//		testGetClassObject();
//		testField();
//		testMethod();
//		testConstructor();
//		testIsInstance();
		try {
			testCast(new Person("sdfsd"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(int.class.getName());
		/*try {
			Constructor<Integer> constructor = int.class.getConstructor(Integer.class);
			Integer integer = constructor.newInstance(null);
			System.out.println(integer);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}*/

		for (Constructor<?> constructor : int.class.getConstructors()) {
			System.out.println("!!!!!");
			for (Class<?> parameterType : constructor.getParameterTypes()) {
				System.out.println(parameterType.getName());
			}
		}
	}
}