package com.spi.inter.impl;

import com.spi.inter.IOperation;

public class PlusOperationImpl implements IOperation {

	@Override
	public int operation(int numberA, int numberB) {
		 return numberA + numberB;
	}
	

}
