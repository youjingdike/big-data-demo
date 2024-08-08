package com.innerclass;

public class OutCls {

    private String name;

    public OutCls(String name) {
        this.name = name;
    }

    public void tst(){
        // TODO 在外部类里创建自己拥有的内部类时,内部类持有外部类的引用
        tst1(new InnerClass());
    }

    public void tst1(InnerClass innerClass){
        innerClass.print();
    }

    public void print(){
        System.out.println("Hello OutCls:"+name);
    }


    class InnerClass {
        public void print() {
            OutCls.this.print();
        }
    }

    public static void main(String[] args) {
        OutCls outCls = new OutCls("tst1");
        outCls.tst();

        outCls = new OutCls("tst2");
        outCls.tst();
    }
}
