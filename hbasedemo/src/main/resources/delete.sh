#!/bin/bash

Shell_Path=$(cd "$(dirname "$0")";pwd)
source /etc/profile
Date_Path=$(date "+%Y-%m-%d-%H-%M-%S-%3N")

# -dt
function delete_table() {
    echo "表删除start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/deleteTable
    grep : ${table_file} | grep -v '^hbase:' > ${Shell_Path}/log/${Date_Path}/deleteTable/tb.log
    for tb in `cat ${Shell_Path}/log/${Date_Path}/deleteTable/tb.log`
    do
      echo "disable '${tb}'
       drop '${tb}'" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>>${Shell_Path}/log/${Date_Path}/deleteTable/log.log 2>&1
        local status=$?
        if [ ${status} != 0 ];then
          echo "删除表：${tb},命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/deleteTable/log.log
          exit 1
        fi
    done
    echo "表删除end..."
}

function delete_namespace() {
    echo "表删除start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/deleteNamespace

    grep : ${table_file} | cut -d ':' -f 1 | grep -v -x 'hbase' | grep -v  -x 'default' | sort| uniq > ${Shell_Path}/log/${Date_Path}/deleteNamespace/ns.log
    for sn in `cat ${Shell_Path}/log/${Date_Path}/deleteNamespace/ns.log`
    do
      echo "drop_namespace '${sn}'" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>>${Shell_Path}/log/${Date_Path}/deleteNamespace/log.log 2>&1
      local status=$?
      if [ ${status} != 0 ];then
        echo "删除表：${sn},命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/deleteNamespace/log.log
        exit 1
      fi
    done
    echo "表删除end..."
}

# -h
function printHelp() {
    echo "功能介绍："
    echo "1.输入相应的参数，执行相应的功能："
    echo "  a.状态检查，请输入参数：-sc hbase客户端的根目录 待检测HBASE集群的配置文件目录"
    echo "  b.导出表信息，请输入参数：-ex hbase客户端的根目录 源端hbase的配置文件目录"
    echo "      说明：不导出hbase命名空间的表"
    echo "  c.数据备份，请输入参数：-sn  hbase客户端的根目录 源端hbase的配置文件目录 要备份的表名称列表文件(全路径) 备份名称前缀"
    echo "      说明：要备份的表名称列表文件，内容示例如下(格式：namespaceName:tableName，每一张表一行)："
    echo "        table1"
    echo "        namespace:table1"
    echo "        namespace:table2"
    echo "  d.数据迁移，请输入参数：-tr hbase客户端的根目录 源端hbase的配置文件目录 目标端hbase的配置文件目录 目标端hbase对应hadoop集群的访问地址 迁移的表及snapshot的名称列表文件(全路径) 是否进行数据校验"
    echo "      说明：迁移的snapshot的名称列表文件，内容示例如下(格式：snapshotName namespace:tablename，每一个备份名称及表名一行,中间用空格分割)："
    echo "        qianyi-snap-default-table1 table1"
    echo "        qianyi-snap-ns1-test1 ns1:test1"
    echo "        qianyi-snap-ns1-test2 ns1:test2"
    echo "  e.执行整个迁移流程，请输入：-all hbase客户端的根目录 源端hbase的配置文件目录 目标端hbase的配置文件目录 目标端hbase对应hadoop集群的访问地址 备份名称前缀"
    echo "        注意：全流程迁移,不处理hbase命名空间的表"
    echo "2.参数说明如下："
    echo "  hbase.root.dir   :  hbase客户端的根目录（可选：如果hbase已经加入到path,可直接执行hbase命令，就不需要配置该参数)"
    echo "  src.hbase.conf   :  源端hbase的配置文件目录 / 待检测HBASE集群的配置文件目录"
    echo "  dst.hbase.conf   :  目标端hbase的配置文件目录"
    echo "  dst.hadoop.uris  :  目标端hbase对应hadoop集群的访问地址"
    echo "  snapshot.prefix  :  备份名称前缀（可选）"
    echo "  table.file       :  要备份的表名称列表文件(全路径)"
    echo "  snapshot.table.file    :  迁移的snapshot的名称列表文件(全路径)"
    echo "  is.data.check    :  是否进行数据校验（可选，默认：true）"
    echo "3.示例  :  "
    echo "  sh backup.sh -all hbase.root.dir=/usr/hbase src.hbase.conf=/root/hbase/conf dst.hbase.conf=/root/hbase1/conf dst.hadoop.uris=hdfs://hdfs-ha snapshot.prefix=qianyi- is.data.check=false"
}

function checkParam() {
 local error_nu=0
 if [ "x"${paramMap['src.hbase.conf']} = "x" ];then
   error_nu=error_nu+1
   echo "请配置源端hbase的配置文件目录参数：src.hbase.conf"
 else
   src_hbase_conf=${paramMap['src.hbase.conf']}
   if [ ! -d "${src_hbase_conf}" ];then
     error_nu=error_nu+1
     echo "src.hbase.conf的配置目录：${src_hbase_conf}不存在或不是目录，请正确配置"
   fi
 fi

 if [ "x"${paramMap['table.file']} = "x" ];then
    error_nu=error_nu+1
    echo "请配置要校验的表名称列表文件(全路径)：table.file"
  else
    table_file=${paramMap['table.file']}
    if [ ! -f "${table_file}" ];then
      error_nu=error_nu+1
      echo "table.file的配置：${table_file}不存在或不是文件，请正确配置"
    fi
  fi

 if [ ${error_nu} != 0 ];then
   exit 1
 fi
}

function parseParam() {
 for i in "$@"; do
     #echo $i
     OLD_IFS="$IFS"
     IFS="="
     local arr=($i)
     IFS="$OLD_IFS"
     #echo "arr的值为：${arr[*]}"
     if [ "${#arr[@]}" -ge 2 ];then
      local ind=${arr[0]}
      #echo "ind@@@@@@:"$ind
      if [ -n "$ind" ];then
        paramMap[$ind]=${arr[1]}
      fi
     fi
 done
 echo "参数如下："
 #echo "${!paramMap[@]}"
 #echo "${paramMap[@]}"
 for key in "${!paramMap[@]}";do
  echo "key:"$key",value:"${paramMap[$key]}
 done

 ##检验是否可执行hbase命令
 if [ "x"${paramMap['hbase.root.dir']} = "x" ];then
   $HBASE_SHELL -help 1>/dev/null 2>&1
   if [ $? != 0 ];then
     echo "hbase命令执行失败，请配置hbase.root.dir"
     exit 1
   fi
 else
   HBASE_SHELL="${paramMap['hbase.root.dir']}/bin/hbase"
   $HBASE_SHELL -help 1>/dev/null 2>&1
   if [ $? != 0 ];then
       echo "hbase命令执行失败，请正确配置hbase.root.dir"
       exit 1
   fi
 fi
}

#放获取到的参数
declare -A paramMap
#声明变量
HBASE_SHELL="hbase"
src_hbase_conf=""
table_file=""

#根据操作类型，选择执行的流程
if [ $# != 0 ]
then
  echo "操作类型：$1"
  op_type=$1
  if [ "-dt" = ${op_type} ];then
    parseParam "$@"
    checkParam
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    delete_table
  elif [ "-dn" = ${op_type} ];then
    parseParam "$@"
    checkParam
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    delete_namespace
  elif [ "-h" = ${op_type} ];then
    printHelp
  else
    echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
  fi
else
   echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
fi
