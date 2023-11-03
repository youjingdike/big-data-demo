package java8;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AnnotationTester {
	/*
	 * 这里有个使用@Repeatable( Filters.class )注解的注解类Filter，Filters仅仅是Filter注解的数组，
	 * 但Java编译器并不想让程序员意识到Filters的存在。
	 * 这样，接口Filterable就拥有了两次Filter（并没有提到Filter）注解
	 */
	@Target( ElementType.TYPE )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface Filters {
        Filter[] value();
    }
     
    @Target( ElementType.TYPE )
    @Retention( RetentionPolicy.RUNTIME )
    @Repeatable( Filters.class )
    public @interface Filter {
        String value();
    };
     
    @Filter( "filter1" )
    @Filter( "filter2" )
    public interface Filterable {        
    }
    
    public static void main(String[] args) {
    	/*
    	 * 反射相关的API提供了新的函数getAnnotationsByType()来返回重复注解的类型
    	 * （请注意Filterable.class.getAnnotation( Filters.class )经编译器处理后将会返回Filters的实例）
    	 */
        for( Filter filter: Filterable.class.getAnnotationsByType( Filter.class ) ) {
            System.out.println( filter.value() );
        }
    }
}

