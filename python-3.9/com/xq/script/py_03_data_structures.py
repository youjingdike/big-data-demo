#--coding:utf-8--


# 元组
# aTuple = ('robert', 77, 93, 'try')
# print aTuple[1:3]
# aTuple[1] = 5


# 列表
# aList = [1,2,3,'a string']
# print aList[0]
# print aList[2:]
# print aList[:3]
# aList[1] = 5
# print aList
 
 
# 字典
aDict = {'host': 'earth'}
aDict['port'] = 80
print(aDict)
print(aDict.keys())
print(aDict.values())
print(aDict['host'])

# 遍历字典
for key in enumerate(aDict):
    print(key, aDict[key])
