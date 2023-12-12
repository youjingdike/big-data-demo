# -*- coding:utf-8 -*-

# import MySQLdb
import pymysql

con = pymysql.connect(host='localhost', user='root', passwd='123123', db='test')

cursor = con.cursor()

sql = "select * from student_infos"

cursor.execute(sql)

# conn连接有两个重要的方法commit【提交新增和修改】,rollback【撤销新增或修改】

row = cursor.fetchone()

print(row)

cursor.close()

con.close()

