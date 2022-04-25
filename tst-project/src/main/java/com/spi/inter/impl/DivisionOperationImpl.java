package com.spi.inter.impl;

import com.spi.inter.IOperation;

public class DivisionOperationImpl implements IOperation {

	@Override
	public int operation(int numberA, int numberB) {
		return numberA / numberB;
	}

}
