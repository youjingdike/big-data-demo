# --coding:utf-8--

# 函数参数传递


# def mytest(num):
#     return num * 2
#
#
# # 不光可以传递变量,还可以传递函数
# def convert(func, seq):
#     print 'convert sequence of numbers to same type'
#     return [func(eachNum) for eachNum in seq]
#
# myseq = [123, 45.67, -6.2e8, 99999999L]
# 面向对象编程说白了就是把对象传来传去，对象是第一要素
# 面向函数编程说白了就是把函数传来传去，函数是第一要素
# print convert(int, myseq)
# print convert(long, myseq)
# print convert(float, myseq)
# print convert(mytest, myseq)


# 闭包
# def my_wrapper(x):
#
#     def wrapper():
#         print "in wrapper"
#         x()
#     return wrapper
#
#
# 装饰器
# @my_wrapper
# def my_deca():
#     print "hello deca"
#
# my_deca()

# 生成器 有点像你熟悉的Iterator
# g = (num*2 for num in myseq)
# for i in g:
#     print i


# 参数的默认赋值
# def taxMe(cost, rate=0.0825):
#     return cost + cost * rate
#
# print taxMe(100)
# print taxMe(100, 0.05)


# def taxMe2(cost, rate=0.0825, *theRest):
#     for eachRest in theRest:
#         print 'another arg:', eachRest
#         cost += eachRest
#     return cost + cost * rate
#
# print taxMe2(100, 0.05, 100, 200,300,400,500,600,700)


# def taxMe3(cost, rate=0.0825, **theRest):
#     for eachRest in theRest.keys():
#         print 'another arg:', eachRest
#         cost += theRest[eachRest]
#     return cost + cost * rate
#
# print taxMe3(100, 0.05, electric=100, water=200, gas=300)


# def taxMes.5, 50, 100, 150, 300, electric=100, water=200, gas=30)
