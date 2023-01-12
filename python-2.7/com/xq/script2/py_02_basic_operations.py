# --coding:utf-8--
 
"""
# \符号可以换行    
weather_is_hot = None
today_is_wednesday = False
if(weather_is_hot != 1) and \
    (today_is_wednesday != 1):
    print 'today is wednesday and weather is hot!'
"""
 
# 三引号的功能
'''Looking for work or have a Python related position 
    that you're trying to hire for? 
    Our community-run job board is the place to go.'''

# 不等于号的表示
# print 1 != 2
# print 1 <> 2

# 与、或、非逻辑符
# print True and False
# print True or False
# print not None


# 赋值
####################
# 方法用法提示等等
####################
def ttt():
    """
    方法用法提示等等
    :return:
    """

    count = 0
    count = count + 1  # 这里count++或者++count无效的
    count += 1
    count -= 1
    count = count * 10
    count *= 10
    name = 'Bob'
    # return name
    x = y = z = 1
    print x, y, z
    x, y, z = 1, 2, 'a string'
    print x, y, z
    x, y = y, x
    # %f,用来输出实数（包括单，双精度），以小数形式输搜索出  %s
    print '%d miles is the same as %.2f km' % (x, y)
    # print x + ' miles is the same as ' + y
    # print str(x) + ' miles is the same as ' + str(y)
#     return x


# 字符串操作
# pystr = 'Python'
# print pystr[0]
# # 所有programmer开发的语言Index都是从0开始！
# # 所有programmer开发的语言都是左闭右开！
# print pystr[2:5]
# print pystr[:2]
# print pystr[3:]
# # -1所有programmer开发的语言都是代表最后一个！
# print pystr[-2]
# print pystr[1:-2]
 
# pystr = 'Python'
# print pystr
# iscool = 'is cool'
# print pystr + iscool
# print pystr + ' ' + iscool
# print pystr * 2
# print '#' * 20
#
# pystr = 1000
# print pystr
#
# print '%s + iscool' % (pystr)
# print str(pystr) + ' is cool'
