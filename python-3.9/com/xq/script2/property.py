#!/usr/bin/python
# -*- coding: UTF-8 -*-
# 描述符类
# class revealAccess:
#     def __init__(self, initval=None, name='var'):
#         self.val = initval
#         self.name = name
#
#     def __get__(self, obj, objtype):
#         print("Retrieving", self.name)
#         return self.val
#
#     def __set__(self, obj, val):
#         print("updating", self.name)
#         self.val = val
#
#
# class myClass:
#     x = revealAccess(10, 'var "x"')
#     y = 5
#
#
# m = myClass()
# print(m.x)
# m.x = 20
# print(m.x)
# print(m.y)


# class Test_name:
#     def __init__(self,name):
#         self.name = name
#     def __get__(self, instance, owner):
#         print("----妥妥的分割线------")
#         print("做一次标记，表示触发__get__")
#     def __set__(self, instance, value):
#         self.value = value
# class Data_test:
#     x  = Test_name("x")
#     def __init__(self):
#         self.x = "xxx"
#
# ttt = Data_test()
# ttt.x      #访问成员属性，触发___get__()方法

#问题5：当我们删除描述类中__set__()方法时，当类描述符对象属性名和实例对象属性名重名的时候还会触发__get__()方法吗？
#答：我们来看一下下边这个例子
# class Test1_name:
#     def __init__(self, name):
#         self.name = name
#     def __get__(self, instance, owner):
#         print("做一次标记，表示触发__get__")
# class Data1_test:
#     xx = Test1_name("x")
#     def __init__(self):
#         self.xx = "此次标记表示，对于非数据描述符，遇到同名属性，实例对象的属性名优先级高于非数据描述符"
#         print("------")
#         print(self.xx)
# ttt = Data1_test()
# ttt.xx  # 访问成员属性，触发___get__()方法

def intNum():
    print("开始执行")
    for i in range(5):
        yield i
        print("继续执行")
num = intNum()

#调用 next() 内置函数
print(next(num))
print('！')
#调用 __next__() 方法
print(num.next())
print('！！')
#通过for循环遍历生成器
for i in num:
    print(i)