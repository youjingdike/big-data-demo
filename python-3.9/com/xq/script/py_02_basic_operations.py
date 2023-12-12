#--coding:utf-8--
 
 
# \符合可以换行    
weather_is_hot = None
today_is_wednesday = None
if(weather_is_hot == 1) and \
    (today_is_wednesday == 1):
    print('today is wednesday and weather is hot!')
 
 
# # 三引号的功能
'''Looking for work or have a Python related position 
    that you're trying to hire for? 
    Our community-run job board is the place to go.'''
     
     
# 不等于号的表示
print(1!=2)
print(1 != 2)
 
 
# 与、或、非逻辑符
print(True and False)
print(True or False)
print(not None)


# 赋值
def test():
    count = 0
    count = count + 1 #这里count++或者++count无效的
    count = count * 10
    count *= 10
    name = 'Bob'
    x = y = z = 1
    x , y , z = 1 , 2 , 'a string'
    print(x, y, z)
    x , y = y , x
    # %f,用来输出实数（包括单，双精度），以小数形式输搜索出
    print('%f miles is the same as %f km' % (x, y))


# 字符串操作
pystr = 'Python'
print(pystr[0])
print(pystr[2:5])
print(pystr[:2])
print(pystr[3:])
print(pystr[-1])
print(pystr[:])

iscool = 'is cool'
print(pystr + iscool)
print(pystr + ' ' + iscool)
print(pystr * 2)
print('#' * 20)

# main函数
if __name__ == '__main__':
    # Helloworld 程序
    print('hello world')
    print ('hello world')
    test()