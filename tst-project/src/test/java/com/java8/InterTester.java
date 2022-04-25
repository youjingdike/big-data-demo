package com.java8;

import org.junit.Test;

import java.util.function.Supplier;

public class InterTester {
	
	@Test
	public void test() {
		/*
		 * 1.函数式接口就是一个具有一个方法的普通接口。像这样的接口，可以被隐式转换为lambda表达式。
		 * 2.需要记住的一件事是：默认方法与静态方法并不影响函数式接口的契约，可以任意使用：
		 */
		Defaulable defaulable = DefaulableFactory.create( DefaultableImpl::new );
	    System.out.println( defaulable.notRequired() );
	         
	    defaulable = DefaulableFactory.create( OverridableImpl::new );
	    System.out.println( defaulable.notRequired() );
	}
	
	/**
	 * 1.函数式接口有且只有一个普通方法
	 * 2.默认方法与静态方法并不影响函数式接口的契约
	 * @author xingqian
	 *
	 */
	@FunctionalInterface
	private interface FunctionalDefaultMethods {
		void method();
		/*
		 * 1.默认方法:子类可以覆写,也可以不覆写
		 * @return
		 */
		default String  defaultMethod() { 
	        return "defaultMethod"; 
	    }
	}
	
	private interface Defaulable {
		/*
		 * 1.默认方法:子类可以覆写,也可以不覆写
		 * 
		 * @return
		 */
		// Interfaces now allow default methods, the implementer may or 
	    // may not implement (override) them.
		default String notRequired() { 
			return "Default implementation"; 
		}
	}
	
	private static class DefaultableImpl implements Defaulable {
	}
	     
	private static class OverridableImpl implements Defaulable {
	    @Override
	    public String notRequired() {
	        return "Overridden implementation";
	    }
	}
	
	private interface DefaulableFactory {
	    // Interfaces now allow static methods
	    static Defaulable create( Supplier< Defaulable > supplier ) {
	        return supplier.get();
	    }
	}
}

