#!/usr/bin/env python
# -*- coding=utf-8 -*-
import redis

# 建立连接
# r = redis.Redis(host='node21',port=6379,db=0)
# r.set('name', 'yasaka')
# print r.get('name')

# 使用数据库连接池
# pool = redis.ConnectionPool(host='node21',port=6379)
# r = redis.Redis(connection_pool=pool)
# r.set('name', 'xuruyun')
# print r.get('name')

# 管道一次发送多个指令
# pool = redis.ConnectionPool(host='node21', port=6379)
# r = redis.Redis(connection_pool=pool)
# pipe = r.pipeline(transaction=True)
# r.set('name', 'yasaka')
# r.set('name', 'liangyongqi')
# pipe.execute()
# print r.get('name')

# 逐行读文件
# index = 0
# f = open('../data/ModelFile.txt')
# while True:
#     line = f.readline()
#     index += 1
#     if not line:
#         break
#     print index,line
# f.close()

# 带缓冲读文件
# index = 0
# times = 0
# f = open('../data/ModelFile.txt')
# while True:
#     lines = f.readlines(1000000)
#     times += 1
#     if not lines:
#         break
#     for line in lines:
#         index += 1
#         print times,index,line
# f.close()

# 连接redis数据库存储文件为Hash散列
pool = redis.ConnectionPool(host='node21',port='6379')
r = redis.Redis(connection_pool=pool)
# f = open('../data/ModelFile.txt')
# f = open('../data/UserItemsHistory.txt')
f = open('../data/ItemList.txt')
while True:
    lines = f.readlines(1000000)
    if not lines:
        break
    for line in lines:
        kv = line[:-2].split('\t')
#         r.hset('rcmd_features_score', kv[0], kv[1])
#         r.hset('rcmd_user_history', kv[0], kv[1])
        r.hset('rcmd_item_list', kv[0], line[:-2])
f.close()
