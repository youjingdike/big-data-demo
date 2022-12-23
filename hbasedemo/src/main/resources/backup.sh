#!/bin/bash

Shell_Path=$(cd "$(dirname "$0")";pwd)
source /etc/profile
# -sc
function statusCheck() {
    echo "环境检查！"
    mkdir -p ${Shell_Path}/log/check
    hbase hbck 1>${Shell_Path}/log/check/log.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "hbase hbck命令执行失败，请查看日志文件："${Shell_Path}/log/check/log.log
      exit 1
    fi
    echo "集群状态："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/Number of live region servers/,/Number of regions in transition/p' ${Shell_Path}/log/check/log.log
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "正常的表："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/Summary:/,/Status:/p' ${Shell_Path}/log/check/log.log | grep 'is okay' | cut -d " " -f 2
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"

    local nu=`grep 'inconsistencies detected' ${Shell_Path}/log/check/log.log | cut -d " " -f 1`
    if [ ${nu} != 0 ];then
      echo "检测出有"$nu"个表不一致，请查看日志文件："${Shell_Path}/log/check/log.log
      exit 1
    else
      echo "没有不一致的表"
    fi
}
# -ex  不导出hbase命名空间的表
function exportTableInfo() {
    echo "导出表信息！"
    mkdir -p ${Shell_Path}/log/tabInfo
    echo "list_namespace" | hbase shell -n 1>${Shell_Path}/log/tabInfo/namespace.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "list_namespace命令执行失败，请查看日志文件："${Shell_Path}/log/tabInfo/namespace.log
      exit 1
    fi

    sed -n '/NAMESPACE/,/row(s)/p'  ${Shell_Path}/log/tabInfo/namespace.log | sed -e '1d' | sed -e '$d' > ${Shell_Path}/log/tabInfo/all_namespace.txt

    echo -n "" > ${Shell_Path}/log/tabInfo/allTable.txt
    for ns in `cat ${Shell_Path}/log/tabInfo/all_namespace.txt`
    do
      if [ "hbase" = ${ns} ];then
        continue
      fi
       echo "list_namespace_tables '$ns'" |  hbase shell -n 1>${Shell_Path}/log/tabInfo/tables.log 2>&1
       status=$?
       if [ ${status} != 0 ];then
         echo "list_namespace_tables '$ns'命令执行失败，请查看日志文件："${Shell_Path}/log/tabInfo/tables.log
         exit 1
       fi
       for tb in `sed -n '/TABLE/,/row(s)/p'  ${Shell_Path}/log/tabInfo/tables.log | sed -e '1d' | sed -e '$d'`
       do
         echo $ns":"$tb>>${Shell_Path}/log/tabInfo/allTable.txt
       done
    done
    cat ${Shell_Path}/log/tabInfo/allTable.txt

    echo "导出的表信息，请查看文件"${Shell_Path}/log/tabInfo/allTable.txt
}
# -sn  目标端不能有重复的表与相同的备份
function snapshot() {
    echo "数据备份！"
    mkdir -p ${Shell_Path}/log/snapshot
    echo -n "" > ${Shell_Path}/log/snapshot/log.log
    echo -n "" > ${Shell_Path}/log/snapshot/snapshot_name.txt
    echo -n "" > ${Shell_Path}/log/snapshot/snap_exits.txt

   ##构建要做的备份名称
   # TODO 这里还是可以设置一个前缀比较好
    for tb in `cat ${Shell_Path}/log/tabInfo/allTable.txt`
      do
        OLD_IFS="$IFS"
        IFS=":"
        arr=($tb)
        IFS="$OLD_IFS"
        echo qianyi-snap-${arr[0]}-${arr[1]}>>${Shell_Path}/log/snapshot/snapshot_name.txt
      done
    echo "要做的备份名称，如下："
    cat ${Shell_Path}/log/snapshot/snapshot_name.txt

    ##start 检查源端是否含有相同的备份,有就报错,打印日志
    echo "list_snapshots 'qianyi-snap-.*'" | hbase shell -n 1>${Shell_Path}/log/snapshot/source_snap_list.log 2>&1
    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/snapshot/source_snap_list.log | sed -e '1d' | sed -e '$d' | cut -d " " -f 2`
    do
      grep -x $sn ${Shell_Path}/log/snapshot/snapshot_name.txt >> ${Shell_Path}/log/snapshot/snap_exits.txt
    done

    local nu=`cat ${Shell_Path}/log/snapshot/snap_exits.txt | wc -l`
    if [ ${nu} != 0 ];then
      echo "源端已经存在的snapshot,如下："
      cat ${Shell_Path}/log/snapshot/snap_exits.txt
      echo "请删除重名的snapshot 或者 重新设置备份名称前缀"
      exit 1
    else
      echo "源端不存在要创建的snapshot..."
    fi
    ##end

    ##start 检查目标端是否含有相同的表及相同的备份,有就跳过,不迁移
    ##end

    exit

    for tb in `cat ${Shell_Path}/log/tabInfo/allTable.txt`
    do
      OLD_IFS="$IFS"
      IFS=":"
      local arr=($tb)
      IFS="$OLD_IFS"
      ##echo "snapshot '$tb','qianyi-snap-${arr[0]}-${arr[1]}'" | hbase shell -n 1>>${Shell_Path}/log/snapshot/log.txt 2>&1
      echo "表"$tb",创建snapshot："qianyi-snap-${arr[0]}-${arr[1]}"的日志如下："
      #hbase snapshot create -t $tb -n qianyi-snap-${arr[0]}-${arr[1]} 1>>${Shell_Path}/log/snapshot/log.log 2>&1
    done

    echo "list_snapshots 'qianyi-snap-.*'" | hbase shell -n 1>${Shell_Path}/log/snapshot/snapInfo.txt 2>&1
    echo "表的备份信息："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/snapshot/snapInfo.txt
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
}
# -tr hbase.rootdir：/apps/hbase/data
function transfer() {
    echo "数据迁移！"
    mkdir -p ${Shell_Path}/log/transfer
    echo "" > ${Shell_Path}/log/transfer/export_sn.log
    echo "" > ${Shell_Path}/log/transfer/restore_sn.log

    ##迁移snapshot
    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/snapshot/snapInfo.txt | sed -e '1d' | sed -e '$d' | cut -d " " -f 2`
    do
      echo $sn
      hbase -Dhdp.version=2.3.7.0-1 org.apache.hadoop.hbase.snapshot.ExportSnapshot -snapshot $sn -copy-to hdfs://hdfs-ha/apps/hbase1/data 1>>${Shell_Path}/log/transfer/export_sn.log 2>&1
    done

    ##restore_snapshot
    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/snapshot/snapInfo.txt | sed -e '1d' | sed -e '$d' | cut -d " " -f 2`
    do
      echo $sn
      echo "restore_snapshots '$sn'" | hbase shell -n 1>>${Shell_Path}/log/transfer/restore_sn.log 2>&1
    done
}
# -dc
function dataCheck() {
    echo "数据校验！"
}
# -dc
function printHelp() {
    echo "功能介绍："
    echo "1.输入相应的参数，执行相应的功能："
    echo "  a.状态检查，请输入参数：-sc hbase客户端的根目录 待检测HBASE集群的配置文件目录"
    echo "  b.导出表信息，请输入参数：-ex hbase客户端的根目录 源端hbase的配置文件目录"
    echo "  c.数据备份，请输入参数：-sn  hbase客户端的根目录 源端hbase的配置文件目录 要备份的表名称列表文件(全路径) 备份名称前缀"
    echo "      说明：要备份的表名称列表文件，内容示例如下(格式：namespaceName:tableName，每一张表一行)："
    echo "        default:table1"
    echo "        namespace:table1"
    echo "        namespace:table2"
    echo "        注意：不备份hbase命名空间的表，如果添加了会剔除掉"
    echo "  d.数据迁移，请输入参数：-tr hbase客户端的根目录 源端hbase的配置文件目录 目标端hbase的配置文件目录 目标端hbase对应hadoop集群的访问地址 迁移的snapshot的名称列表文件(全路径)"
    echo "      说明：迁移的snapshot的名称列表文件，内容示例如下(格式：snapshotName，每一个备份名称一行)："
    echo "        qianyi-snap-ns1-test1"
    echo "        qianyi-snap-ns1-test2"
    echo "  e.数据校验，请输入参数：-dc"
    echo "  f.执行整个流程，请输入：-all hbase客户端的根目录 源端hbase的配置文件目录 目标端hbase的配置文件目录 目标端hbase对应hadoop集群的地址 备份名称前缀"
    echo "2.参数说明如下："
    echo "  hbase.root.dir     :  hbase客户端的根目录（可选：如果已经加入到path,可直接执行hbase命令)"
    echo "  src.hbase.conf     :  源端hbase的配置文件目录/待检测HBASE集群的配置文件目录"
    echo "  dst.hbase.conf     :  目标端hbase的配置文件目录"
    echo "  dst.hadoop.uris    :  目标端hbase对应hadoop集群的地址"
    echo "  snapshot.prefix    :  备份名称前缀（可选）"
    echo "  snapshot.table.file:  要备份的表名称列表文件(全路径)"
    echo "  snapshot.list.file :  迁移的snapshot的名称列表文件(全路径)"
    echo "3.示例  :  "
    echo "  sh backup.sh -all hbase.root.dir=/usr/hbase src.hbase.conf=/root/hbase/conf dst.hbase.conf=/root/hbase1/conf dst.hadoop.uris=hdfs://hdfs-ha snapshot.prefix=qianyi-"
}

function all(){
  statusCheck
  exportTableInfo
  snapshot
  transfer
  dataCheck
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
}

param_count=$#
#放获取到的参数
declare -A paramMap
parseParam "$@"
exit
if [ $# != 0 ]
then
  echo "操作类型：$1"
  op_type=$1
  if [ "-sc" = ${op_type} ];then
    statusCheck
  elif [ "-ex" = ${op_type} ];then
    exportTableInfo
  elif [ "-sn" = ${op_type} ];then
    snapshot
  elif [ "-tr" = ${op_type} ];then
    transfer
  elif [ "-dc" = ${op_type} ];then
    dataCheck
  elif [ "-all" = ${op_type} ];then
      all
  elif [ "-h" = ${op_type} ];then
      printHelp
  else
    echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
  fi
else
   echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
fi
