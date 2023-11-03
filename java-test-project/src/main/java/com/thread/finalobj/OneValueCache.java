package com.thread.finalobj;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 不可变容器类
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;


    public OneValueCache(BigInteger i, BigInteger[] factors) {
        this.lastNumber = i;
        this.lastFactors = Arrays.copyOf(factors,factors.length);//如果这里不调用copyof,那么OneValueCache就不是不可变的
    }

    public BigInteger[] getFactors(BigInteger integer) {
        if (lastNumber ==  null || !lastNumber.equals(integer)) {
            return null;
        } else {
            return Arrays.copyOf(lastFactors, lastFactors.length);//如果这里不调用copyof,那么OneValueCache就不是不可变的
        }

    }

}
