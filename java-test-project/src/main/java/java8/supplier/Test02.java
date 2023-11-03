package java8.supplier;

import java.util.Objects;
import java.util.function.Supplier;

public class Test02 {
	
	public static SunPower produce(Supplier<SunPower> supp) {  
        return supp.get();  
    }  
  
    public static void main(String[] args) {  
        SunPower power = new SunPower();  
  
        SunPower p1 = produce(() -> power);//只会初始化一次 因此只会输出一个结果  
        SunPower p2 = produce(() -> power);  
        SunPower p3 = produce(SunPower::new);  


        String t = "test";
        System.out.println("Check the same object? " + Objects.equals(p1, p2));
        Supplier<SunPower> supplier = ()->new SunPower(t);
        SunPower1 sunPower1 = new SunPower1(supplier::get);
        System.out.println("Check the same object? " + Objects.equals(sunPower1.get(), sunPower1.get()));
    }
}

class SunPower {  
    public SunPower() {
        System.out.println("Sun Power initialized..");
    }
    public SunPower(String t) {
        System.out.println("Sun Power initialized.."+t);
    }
}

class SunPower1 {
    private Supplier<SunPower> supplier;
    public SunPower1(Supplier<SunPower> supplier) {
        this.supplier = supplier;
        System.out.println("Sun Power1 initialized..");
    }

    public SunPower get(){
        return supplier.get();
    }
}