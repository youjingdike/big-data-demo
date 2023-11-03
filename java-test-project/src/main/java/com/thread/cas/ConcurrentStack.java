package com.thread.cas;

import java.util.concurrent.atomic.AtomicReference;

public class ConcurrentStack<E> {
    AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();

    public void push(E item) {
        Node<E> newNode = new Node<>(item);
        Node<E> oldNode;
        do {
            oldNode = top.get();
            newNode.next = oldNode;
        } while (!top.compareAndSet(oldNode, newNode));
    }

    public E pop(){
        Node<E> newNode;
        Node<E> oldNode;
        do {
            oldNode = top.get();
            if (oldNode == null) {
                return null;
            }
            newNode = oldNode.next;
        } while (!top.compareAndSet(oldNode, newNode));
        return oldNode.item;
    }

    private static class Node<E> {
        private final E item;
        public Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
