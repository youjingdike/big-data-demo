#!/usr/bin/env python
# coding=utf-8

class MyClass:
    def __init__(self,x,y):
        self.x = x
        self.y = y
        print(self.__class__.__name__,self.x,self.y)

if __name__ == '__main__':
    my_class = type("MyClass",(),{"x":1,"y":2})
    my_ins = my_class()
    # print(my_ins)
    # print(my_ins.x,my_ins.y)
    print("@@@@@")

    clsName = globals().get("MyClass")
    obj = clsName(1,2)
    # print(obj.x,obj.y)

    print("!!!!!")
    module = __import__("py_18_create_instance")
    cls_name = getattr(module,"MyClass")
    obj_ins = cls_name(1,2)
    # print(obj_ins.x,obj_ins.y)