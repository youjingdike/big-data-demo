#!/usr/bin/env python
# coding=utf-8

# 自定义函数，包含*args和**kwargs
# 注意：这里的args,kwargs是可以换成其他名称的，不是固定的
def func(argument, *args, **kwargs):
    print("func !!!!!!!!")
    print("argument:" + str(argument))
    print("args:" + str(args))
    print("kwargs:" + str(kwargs))


# 定义元组以及字典类型
tuple = (1, 2)
dict = {
    'a': 1,
    'b': 2
}

# 调用函数，传入argument、args以及kwargs参数
func(1, 2, a=3)
# 调用函数，传入参数
# 注意位置参数和关键字参数的顺序

# 这个属于关键字参数调用
func(*tuple, **dict)
# 这个属于位置参数调用
func(tuple, dict)

print("###########")


# 调用函数，argument参数必须使用关键字参数调用
def func1(*args, argument, **kwargs):
    print("func1  @@@@@@@@@@@")
    print("args:" + str(args))
    print("argument:" + str(argument))
    print("kwargs:" + str(kwargs))


# 调用函数，传入argument、args以及kwargs参数
# func1(1, 2, a=3) # 这个会报错
func1(1, 2, 3, 2, 3, 3, argument=2, a=3)
# 调用函数，传入参数
# 注意位置参数和关键字参数的顺序
func1(*tuple, argument=5, **dict)  # 这个属于关键字参数调用
func1(*tuple, 50,argument=5, **dict)  # 这个属于关键字参数调用
# func1(*tuple, **dict)  # 这个报错
func1(tuple,51, argument=9, **dict)
func1(tuple,52, argument=9)
# func1(tuple,argument=9,dict) # 这个会报错

print("*************************")


def func2(*args, argument=8, **kwargs):
    print("func2  @@@@@@@@@@@")
    print("args:" + str(args))
    print("argument:" + str(argument))
    print("kwargs:" + str(kwargs))
# 调用函数，传入argument、args以及kwargs参数
func2(1, 2, 3, 2, 3, 3, a=3)
func2(1, 2, 3, 2, 3, 3, argument=2, a=3)
# 调用函数，传入参数
# 注意位置参数和关键字参数的顺序
# 这个属于关键字参数调用
func2(*tuple, argument=5, **dict)
func2(*tuple, argument=5)
# func2(*tuple, argument=5,dict) # 这个会报错
# 这个属于位置参数调用
func2(tuple, 9, dict)  # 这个会把所有的参数都传给args

print("*************************")

def func3(argument=8,*args, **kwargs):
    print("func3  @@@@@@@@@@@")
    print("argument:" + str(argument))
    print("args:" + str(args))
    print("kwargs:" + str(kwargs))
# 调用函数，传入argument、args以及kwargs参数
func3(1, 2, 3, 2, 3, 3, a=3)
# func3(1, 2, 3, 2, 3, 3, argument=50,a=3) # 这个运行会报错
# 调用函数，传入参数
# 注意位置参数和关键字参数的顺序
# 这个属于关键字参数调用
func3(1,*tuple, **dict)
func3(*tuple, **dict)
func3(tuple, **dict)
func3(tuple, dict)
func3(argument=5,**dict)
# func3(*tuple, argument=5,dict) # 这个会报错
# 这个属于位置参数调用
func3(tuple, 9, dict)  # 这个会把后两个的参数都传给args