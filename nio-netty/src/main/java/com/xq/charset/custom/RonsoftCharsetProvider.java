package com.xq.charset.custom;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.HashSet;
import java.util.Iterator;

public class RonsoftCharsetProvider extends CharsetProvider {
    private static final String CHARSET_NAME = "X-ROT13";
    private Charset rot13;

    public RonsoftCharsetProvider() {
        this.rot13 = new Rot13Charset(CHARSET_NAME,new String[0]);
    }

    @Override
    public Iterator<Charset> charsets() {
        HashSet<Charset> set = new HashSet<>(1);
        set.add(rot13);
        return set.iterator();
    }

    @Override
    public Charset charsetForName(String charsetName) {
        if (charsetName.equalsIgnoreCase(CHARSET_NAME)) {
            return rot13;
        }
        return null;
    }
}
