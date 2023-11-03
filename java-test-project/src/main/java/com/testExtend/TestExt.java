package com.testExtend;

import java.util.ArrayList;
import java.util.List;

public class TestExt extends Mme{
	String ss = "";
	String s = "";
	
	public TestExt() {
		super();
		System.out.println("我是构造器");
	}
	
	public TestExt(String s) {
		super();
		this.ss = s;
	}

	public String getS() {
		return ss;
	}
	public void te() {
	    List<String> list = new ArrayList<String>();
	    for (int i = 0; i < 2; i++) {
	        ss = "1";
//	        list.add(ss);
	    }
	}
	public void setS(String s) {
		this.ss = s;
	}

	public static void main(String[] args) {  
		TestExt tt = new TestExt("s");
		System.out.println(tt.getS()=="s");
		System.out.println(tt.s);
		TestExt t1 = new TestExt();
		Ttt n = new Ttt();
		n.hao(t1);
   
	    List<Me> meList = new ArrayList<Me>();
//	    List meList = new ArrayList();
	    Ttt ttt = new Ttt();
	    Imme imme = new Imme();
	    Me ttt1 = new Ttt();
        Me imme1 = new Imme();
	    meList.add(ttt1);
	    meList.add(imme1);
	    System.out.println(meList.indexOf(ttt1));
	    for (int k = 0; k < meList.size(); k++) {
	        Me me = (Me)meList.get(k);
	        me.mmm();
	    }
	    meList.remove(ttt1);
	    for (int k = 0; k < meList.size(); k++) {
	        Me me = (Me)meList.get(k);
	        if (me instanceof Ttt) {
	            Ttt tt1 = (Ttt)me;
	            tt1.say();
	        }
	        if (me instanceof Imme) {
                Imme tt1 = (Imme)me;
                tt1.say();
            }
        }
	    
	    You you = new You();
	    you.me();
	    You youExd = new YouExd();
	    youExd.me();
	}
	
    @Override
    void mn() {
        
    }
    
}

interface Me {
	String S = "wre";
	void m();
	void nn();
	void mmm();
}

class Ttt implements Me{
	public void hao(TestExt t) {
		System.out.println(t.ss);
	}


    public void mmm() {
        System.out.println("我是Ttt类！");
    }

    public void nn() {
        
    }
    
    public void say() {
        System.out.println("我是Ttt的新方法");
    }


    public void m() {
        
    }
}

class Imme implements Me {

    public void m() {
        
    }

    public void mmm() {
       System.out.println("我是Imme类！");
        
    }

    public void nn() {
        
    }
    
    public void say() {
        System.out.println("我是Imme的新方法");
    }
}

abstract class Ime implements Me {

    public void m() {
        
    }
    
}

class Imme1 extends Ime {

    public void mmm() {
        
    }

    public void nn() {
        
    }

}

abstract class Mme implements Me{

    public void m() {
        
    }

    public void mmm() {
        
    }

    public void nn() {
        
    }
    
    abstract void mn();
    
}


abstract class Mmme extends Mme {
    @Override
    void mn() {
        
    }
}

class Mmmme extends Mmme {

    @Override
    void mn() {
        
    }
    
}

/*abstract*/ class You {
    public static void main(String[] args) {
        
    }
    /*abstract void mm();*/
    void me() {
        System.out.println("我是超类的方法！");
    }
}

class YouExd extends You{
    void me() {
        System.out.println("我是子类的方法！");
    }

}