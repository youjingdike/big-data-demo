# coding=utf-8

import os
import shutil

d = 'E:\\workspacePy\\HelloPython\\test_test'
for i in os.listdir(d):
    # 因为windows系统不是utf8编码,所以需要decode一下
    # print i.decode('GBK')
    new_file = i.replace('part-r-0000', 'part')
    old_full_file = d + '\\' + i
    new_full_file = d + '\\' + new_file
#     print old_full_file.decode('GBK'), new_full_file.decode('GBK')
    shutil.move(old_full_file, new_full_file)
print('Done!')
