#!/usr/bin/python
# -*- coding: UTF-8 -*-
# 文件名: py_16_deadlock.py

from threading import Thread, Lock
import time

__author__ = 'yasaka'


def work1():
    lock1.acquire()
    time.sleep(1)
    for i in range(5):
        print "work1"
        lock2.acquire()
        print i
        lock2.release()
    lock1.release()


def work2():
    lock2.acquire()
    time.sleep(1)
    for i in range(5):
        print "work2"
        lock1.acquire()
        print i
        lock1.release()
    lock2.release()


lock1 = Lock()
lock2 = Lock()

t1 = Thread(target=work1)
t2 = Thread(target=work2)
t1.start()
t2.start()

# 1, 同步--避免死锁的出现
# 2, lock2.acquire(timeout=)
# 3, 银行家算法

