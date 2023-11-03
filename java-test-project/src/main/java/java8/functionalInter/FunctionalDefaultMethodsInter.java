package java8.functionalInter;
/**
 * 1.函数式接口只能有一个普通抽象方法
 * 2.默认方法与静态方法并不影响函数式接口的契约
 * @author xingqian
 *
 */
@FunctionalInterface
public interface FunctionalDefaultMethodsInter {
	void method();
	
    default void defaultMethod() {            
    }
    
    static void staticMethod() {
    }
    
}
