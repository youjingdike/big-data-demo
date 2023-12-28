#!/usr/bin/python
# -*- coding: UTF-8 -*-

# 以下两种装饰器写法不同，效果相同
# 写法一，适合没有返回值的函数
# def decorate_func(old_func):  # 装饰器
#     def new_func():  # 新函数
#         print("Hello!写法一")
#         old_func()
#         print("Bye！写法一")
#
#     return new_func


# 写法二，适合有返回值的函数
# 函数没有返回值也可以使用该写法。
def decorate_func(old_func):  # 该写法更规范
    def new_func():  # 新函数
        print("Hello!写法二")
        result = old_func()
        print("Bye！写法二")
        return result

    return new_func


# 装饰器（已知函数参数列表）
def decorate_func_params(old_func):  # 该写法更规范
    def new_func(a, b):  # 新函数
        print("开始计算1：")
        result = old_func(a, b)  # 函数执行后返回结果
        print("计算结束1。")
        return result

    return new_func


def decorate_func_any_params(old_func):
    def new_func(*args, **kwargs):  # 对参数列表进行装包
        print("开始计算2：")
        result = old_func(*args, **kwargs)  # 对参数列表进行拆包
        print("计算结束2。")
        return result

    return new_func


def decorate_param(param):  # 装饰器传入参数
    def decorate_func(old_func):  # 装饰器传入函数
        def new_func(*args, **kwargs):
            print("hello")
            print(f"装饰器参数：{param}")  # 装饰器参数param
            result = old_func(*args, **kwargs)
            print("bye!")
            return result

        return new_func

    return decorate_func


@decorate_func  # 经典用法 @装饰器名
def func1():
    print("零否！")


@decorate_func  # 经典用法 @装饰器名
def func2():
    print("源源不断！")


@decorate_func_params
def func3(a, b):
    sum = a + b
    return sum


@decorate_func_any_params
def func4():
    print("源源不断！func4")


@decorate_func_any_params
def func5(a, b):
    sum = a + b
    return sum


@decorate_param("零否")
def func6(a, b):
    sum = a + b
    print(f'{a} + {b} = {sum}')
    return sum


@decorate_param(None)
def func7(a, b):
    sum = a + b
    print(f'{a} + {b} = {sum}')
    return sum


func1()
print("------")
func2()
'''
等同于
def func1():
    print("零否！")

decorate_func(func1)()
'''
print("------")
f3 = func3(2, 6)
print(f3)
'''
#等同于
def func3(a, b):
    sum = a + b
    return sum
    
decorate_func_params(func3)(2, 6)
'''
print("------")
func4()
print("------")
print(func5(2, 7))
print("------")
print(func6(1, 2))
'''
#等同于
def func6(a, b):
    sum = a + b
    print(f'{a} + {b} = {sum}')
    return sum

decorate_param("零否")(func6)(1,2)
'''

print("------")
print(func7(1, 2))


def decorate_func1(old_func):
    def new_func(*args, **kwargs):
        print("装饰器1--hello")
        result = old_func(*args, **kwargs)
        print("装饰器1--bye!")
        return result

    return new_func


def decorate_func2(old_func):
    def new_func(*args, **kwargs):
        print("装饰器2--hello")
        result = old_func(*args, **kwargs)
        print("装饰器2--bye!")
        return result

    return new_func


@decorate_func1
@decorate_func2
def func8():
    print("零否！func8")


@decorate_func2
@decorate_func1
def func9():
    print("源源不断！func9")


func8()
'''
#等同于
def func8():
    print("零否！")

decorate_func1(decorate_func2(func8))()
'''
print('------------')
func9()
