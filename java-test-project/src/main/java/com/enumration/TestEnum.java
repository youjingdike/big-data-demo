package com.enumration;

public class TestEnum {
    public static void main(String[] args) {
        System.out.println(Me.A);
        String str = "A";
        Me f = Me.valueOf(str);
        Me f1 = Me.getMe("a");
        
        
        System.out.println("f.ordinal():"+f.ordinal());
        System.out.println("f.name():"+f.name());
        System.out.println("f.getName():"+f.getName());
        checkEnum(f);
        if (Me.A.equals(f1)) {
            System.out.println("equal");
        } else {
            System.out.println("no equal");
        }
        
        //values()方法api里没有
        Me[] values = Me.values();
        for (Me me : values) {
			System.out.println(me.getName());
		}
    }
    
    public static void checkEnum(Me m) {
        switch(m){
            case A:System.out.println("wo shi " + m.getName());
                    break;
            case B:System.out.println("wo shi " + m.getName());
                    break;
            case C:System.out.println("wo shi " + m.getName());
                    break;
            default:System.out.println("wo shi default");
        }
    }
}

enum Me {
    A("a"),B("b"),C("c"),D("");
    
    private String name = "";

    private Me(String name) {
        this.name = name;
    }

    //可以自定义一些方法
    public String getName() {
        return name;
    }
    
    public static Me getMe(String name) {
    	for (Me me : values()) {
			if (me.name.equals(name)) {
				return me;
			}
		}
    	return null;
    }
}