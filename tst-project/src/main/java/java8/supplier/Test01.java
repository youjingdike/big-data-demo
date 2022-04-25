package java8.supplier;

import java.util.function.Supplier;

public class Test01 {
	public static void main(String[] args) {  
        Supplier<String> supplier=()->"Supplier";//定义了输出结果 因此没有必要重写toString()  
        System.out.println(supplier.get());  
        
        SunPower power = new SunPower();
        
        Supplier<SunPower> sunPower = new Supplier<SunPower>() {
			@Override
			public SunPower get() {
				return power;
			}
		};
    }
}
