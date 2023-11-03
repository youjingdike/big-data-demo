package com.testExtend;

public class JuniorClass extends SeniorClass {
    private String name;
    public JuniorClass(){
      super(); //Automatic call leads to NullPointerException
      name = "JuniorClass";
    }
    public String toString(){
        return "1";
//      return name.toUpperCase();
    }
    
    public static void main(String[] args) {
        new JuniorClass();
        new M1e();
    }
}

class M1e extends JuniorClass {

    @Override
    public String toString() {
        return "2";
    }
    
}