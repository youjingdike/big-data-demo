package com.thread;

import java.awt.*;
import java.util.EventListener;

public class SafeListener {
    private final EventListener eventListener;
    private SafeListener() {
        eventListener = new EventListener() {
            public void onEvent(Event e) {
//                doSomething(e);
            }
        };
    }

    /*public static SafeListener newInstance(EventSource source) {
        SafeListener safeListener = new SafeListener();
//        source.registerListener(safeListener.eventListener);
        return safeListener;
    }*/
}
