#coding=utf-8

import sys
# 列出下面的方法属性
# print dir(sys)


# 查看帮助
# help("keywords")
# 查看import关键字如何使用
# help("import")
# 查看模块的使用
# help("os.path")
# 查看list如何使用
# help("list")
# 查看字符串中find方法使用
# help("str.find")
# 查看内置函数如何使用
# help("open")


# 字符串数字之间转换
print int("6")
print str(6)


# 字符串长度方法
foo = 'abc'
print len(foo)


# open()打开文件操作
poem="让我们一起学习Python！444"
f=open('../testtest/part-r-0.txt','a') # open for 'w'riting
f.write(poem.decode('utf-8').encode('gbk')) # write text to file
f.close() # close the file


# range() 一组数字
print range(10)
print range(1,10,3)
  
foo = 'abc'
for i in range(len(foo)):
    # %d,用来输出十进制整数
    print foo[i], '(%d)' % i 
      
  
# enumerate()
aDict = {'host': 'earth'}
aDict['port'] = 80
for i, key in enumerate(aDict):
    print i, key, aDict[key]
     
     
# type()获得对象类型
print type(aDict)
print isinstance(6, int)
print isinstance(aDict, dict)
print isinstance(aDict, list)