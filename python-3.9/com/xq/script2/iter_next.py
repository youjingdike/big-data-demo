#!/usr/bin/python
# -*- coding: UTF-8 -*-

def intNum():
    print("开始执行")
    for i in range(5):
        print("yield 之前")
        yield i
        print("yield 之后，继续执行")


num = intNum()
print("!")
# 调用 next() 内置函数
print(next(num))
print('！!')
# 调用 __next__() 方法
print(num.__next__())
# print(num.next()) # python2的写法
print('！！!')
# 通过for循环遍历生成器
for i in num:
    print("###")
    print(i)
    print("@@@")
