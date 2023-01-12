# -*- coding:utf-8 -*-

from pymongo import MongoClient

import random

# client = MongoClient()
client = MongoClient('node21', 27017)
# client = MongoClient('mongodb://localhost:27017/')
db = client['yasaka']  # 连接库
collection = db['class1']

# 用户认证
collection.drop()
# 删除集合mine
# JSON BSON
collection.save({'id': 1, 'name': 'kaka', 'sex': 'male'})

db.class2.drop()
# 插入一个数据
for id in range(2, 10):
    name = random.choice(['steve', 'koby', 'owen', 'tody', 'rony'])
    sex = random.choice(['male', 'female'])
    db.class2.insert({'id': id, 'name': name, 'sex': sex})
# 通过循环插入一组数据
content = db.class2.find({'name': 'owen'})
# 打印所有数据
for i in content:
    print i

client.close()
