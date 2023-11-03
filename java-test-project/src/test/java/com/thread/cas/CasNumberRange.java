package com.thread.cas;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.junit.runner.notification.RunListener;

import java.util.concurrent.atomic.AtomicReference;

@RunListener.ThreadSafe
public class CasNumberRange {
    @Immutable
    private static class IntPair{
        final int lower;
        final int upper;

        private IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values =
            new AtomicReference<>(new IntPair(0, 0));

    public int getLower(){
        return values.get().lower;
    }

    public int getUpper(){
        return values.get().upper;
    }

    public void setLower(int lower){
        while (true) {
            IntPair oldV = values.get();
            if (lower>oldV.upper) {
                throw new IllegalArgumentException("Can't set lower to "+lower+" > upper");
            }

            IntPair newV = new IntPair(lower, oldV.upper);
            if (values.compareAndSet(oldV, newV)) {
                return;
            }
        }
    }

    public void setUpper(int upper){
        while (true) {
            IntPair oldV = values.get();
            IntPair newV = new IntPair(oldV.lower,upper);
            if (values.compareAndSet(oldV, newV)) {
                return;
            }
        }
    }
}
