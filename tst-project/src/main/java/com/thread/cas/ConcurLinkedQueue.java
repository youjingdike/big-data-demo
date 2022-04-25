package com.thread.cas;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ConcurLinkedQueue<E> {
    private static class Node<E> {
        private final E item;
        private volatile Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }

    private static AtomicReferenceFieldUpdater<Node,Node> nextUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Node.class,Node.class,"next");

}
