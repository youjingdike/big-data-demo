#!/usr/bin/python
# -*- coding: UTF-8 -*-
# 文件名: hello_spark.py

# __author__ = 'yasaka'

"""
1, 解压这个spark-1.6.0-bin-hadoop2.4.tgz.gz
2, 从目录spark-1.6.0-bin-hadoop2.4拷贝python文件夹下面的pyspark到C:\Python27\Lib\site-packages下
3, 在你对应的py脚本 Edit Configuration里面 Configuration标签下面的 Environment Variables里面
   SPARK_HOME=E:\StudyMaterials\spark01\spark-1.6.0-bin-hadoop2.4
4, cmd进入到目录C:\Python27\Scripts下面 执行，pip install py4j

"""

"""
#!/bin/bash  
  
hadoop jar /usr/hadoopsoft/hadoop-2.5.1/share/hadoop/tools/lib/hadoop-streaming-2.5.1.jar \  
        -input /user/xxx/output/qianjc/python/input \  
        -output /user/xxx/output/qianjc/python/output \  
        -file wc_map.py \  
        -file wc_reduce.py \  
        -mapper "python wc_map.py" \  
        -reducer "python wc_reduce.py" \  
        -jobconf mapred.reduce.tasks=1 \  
        -jobconf mapred.job.name="qianjc_test" 
"""

from pyspark import SparkContext

spark = SparkContext(master="local", appName="helloSpark")

print spark.textFile("../test_test/text.txt").flatMap(lambda line: line.split()).map(lambda word: (word, 1)).reduceByKey(lambda a, b: a+b).collect()








