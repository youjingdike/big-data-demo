package com.spi.test;

import com.spi.inter.IOperation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

public class Test {
	public static void main(String[] args) {
		/*IOperation plus = new PlusOperationImpl();  
		  
        IOperation division = new DivisionOperationImpl();  
  
        System.out.println(plus.operation(6, 3));  
  
        System.out.println(division.operation(6, 3));  
        System.out.println("~~~~~~~~~~~~~");*/
//        System.out.println("classPath:"+System.getProperty("java.class.path"));

        ServiceLoader<IOperation> operations = ServiceLoader.load(IOperation.class);
        for (IOperation operation : operations) {
            System.out.println(operation.getClass().getName());
            System.out.println("res:"+operation.operation(6, 3));
        }
System.out.println("1~~~~~~~~~~~~~~~~~~~~~");
        Iterator<IOperation> operationIterator = operations.iterator();
        while (operationIterator.hasNext()) {
            IOperation operation = operationIterator.next();

            System.out.println(operation.getClass().getName());
            System.out.println("res:"+operation.operation(6, 3));

        }
System.out.println("2~~~~~~~~~~~~~~~~~~~~~");

        List<IOperation> result = new LinkedList<>();
        Iterator<IOperation> operationIterator1 = operations.iterator();
        operationIterator1.forEachRemaining(result::add);//会执行游标，下面再使用该游标就失效了。
        System.out.println("result size:"+result.size());

System.out.println("3~~~~~~~~~~~~~~~~~~~~~");
        while (operationIterator1.hasNext()) {
            IOperation operation = operationIterator1.next();

            System.out.println(operation.getClass().getName());
            System.out.println(operation.operation(6, 3));

        }
        System.out.println("@@@@@");
        result.forEach(operation->{
            System.out.println(operation.getClass().getName());
            System.out.println(operation.operation(6, 3));
        });
	}
}
