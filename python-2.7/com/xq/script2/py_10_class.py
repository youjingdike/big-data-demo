# coding=utf-8

# public 
# private
# protected

# 单下划线、双下划线、头尾双下划线说明：
# __foo__: 定义的是特列方法，类似 __init__() 之类的。
# _foo: 以单下划线开头的表示的是 protected 类型的变量，即保护类型只能允许其本身与子类进行访问，不能用于 from module import *
# __foo: 双下划线的表示的是私有类型(private)的变量, 只能是允许这个类本身进行访问了。



class AddrBookEntry(object):
    myVersion = '1.0'  # 类属性

    # __slots__ = ['name']

    # 定义构造方法
    def __init__(self, name, phone):
        self.name = name
        self.phone = phone
        self.__extra = 'xxx'
        self._protect = 'pppp'
        # print 'Created instance for:', self.name

    # 定义方法
    def updatePhone(self, new_phone):
        self.phone = new_phone
        print 'Updated phone# for:', self.name

    def __add__(self, other):
        print self.name + ' ' + other.name

    def __str__(self):
        return "to string"

    def __getattribute__(self, item):
        print "__getattribute__ is called() %s" % item
        return object.__getattribute__(self, item)
    def tst(self,command_name):
        print '@@'
        self_methods = dir(self)
        for command_name1 in self_methods:
            print command_name1
        method = getattr(self, command_name)
        print method
        #method("tst1")
# class Addr(AddrBookEntry):
#     pass
#
#
# # 创建实例
print '!!!!'
john = AddrBookEntry('John', '123')
print '!!!!'
# jane = AddrBookEntry('Jane', '456')
#
john.tst("tst")

# print john
# print john.name
# print john.phone
# print john.name, john.phone
# print jane.name, jane.phone
# john.updatePhone("000")
# print john.name, john.phone
# print AddrBookEntry.myVersion
#
# john.gender = "male"
# print john.gender
#
# addr = Addr("yasaka", "186")
# print addr._protect
# # print addr.__extra
#
# john + jane
  
# print bool(None)

# print 1/2
# print 3.0/2.0
# print 1//2
# print 3.0//2.0
    
# a, b = divmod(15, 6)
# print a, b

# a = ("one", "two")
# print a[0]
# b = "just-one",
# print b[0]
# c = ("just-one")
# print c[0]
# d = "just-one"
# print d[0]
  
   
# def func(args):
#     args.append(1)
#     print args
#
# # func("ddd")
# mylist = ["ddd"]
# func(mylist)
# func(mylist)
