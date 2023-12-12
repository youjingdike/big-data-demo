# --coding:utf-8--
from random import randint as shanshanzi

# del
# aList = [1,2,3]
# del aList
# print aList

# help("list.sort")

# lambda 排序
# data = []
# data.append({"province": "shanghai", "order_price": 200, "user_count": 50, "total_price": 10000})
# data.append({"province": "beijing", "order_price": 100, "user_count": 100, "total_price": 10000})
# data.append({"province": "shenzhen", "order_price": 300, "user_count": 100, "total_price": 30000})
# # sort排序
# data.sort(key=lambda y: (y['user_count'], -y['total_price']), reverse=True)
# for d in data:
#     print d


# 对象比较支持多个比较操作
# print 3 < 4 < 7  # 相当于 (3<4) and (4<7)
# print 4 > 3 == 3  # 相当于 (4>3) and (3==3)
# print 4 < 3 < 8 != 2 < 7    # 从左到右


# Java中的Collections.reverse(arrayList);
# foolist = [123, 'abc', 342.23, 'xyz']
# print foolist[::-1]
# print foolist[::-2]
# Java中的Collections.sort(arrayList);
# print sorted(foolist)


# is 对象的内存最终指向的地址  
# == 对象最终指向的数值
# foo1 = foo2 = 4.3
# print foo1 == foo2
# print foo1 is foo2

# foo1 = 4.3
# foo2 = foo1
# print foo1 == foo2
# print foo1 is foo2
#
# print id(foo1)
# print id(foo2)


# foo1 = 4.3
# foo2 = 1.3 + 3.0
# print foo1 == foo2
# print foo1 is foo2
#
# print id(foo1)
# print id(foo2)


# cmp(a,b) 比较大小 -1, 0 ,1
# print cmp(7, 7)
# print cmp("abs", "azs")


# round() 四舍五入
# print round(100.10)
# print round(100.49)
# print round(100.50)


# random 取随机数
# print shanshanzi(1, 100)

# in 操作
# print 'bc' in 'abcd'
# print 'n' in 'abcd'
# print 'nm' not in 'abcd'


# string 模块
# import string
# print string.capitalize("hello")
# print string.lowercase
# print "HELLO".lower()
# print "hello".upper()
# print string.split("asdadada asdada")
# print string.rstrip("              adsd         ")
# print string.lstrip("              adsd         ")
# print string.strip("              ad  sd         ")


# re 模块
import re
# Python提供了两种不同的原始操作:match和search.match是从字符串的起点开始做匹配,而search是从字符串做任意匹配.
# m = re.search("^ab+", "asdfabbbb")
# print m
# m = re.search("ab+", "asdfabbbb")
# print m
# print m.group()
# m = re.match("c", "cabcdef")
# print m.group()
# pattern = re.compile("c")
# m = pattern.match("cabcdef")
# print m.group()
