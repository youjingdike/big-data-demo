#!/bin/bash

Shell_Path=$(cd "$(dirname "$0")";pwd)
source /etc/profile
Date_Path=$(date "+%Y-%m-%d-%H-%M-%S-%3N")

# -sc/-all
function statusCheck() {
    echo "环境检查start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/check
    $HBASE_SHELL --config ${src_hbase_conf} hbck 1>${Shell_Path}/log/${Date_Path}/check/log.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "hbase hbck命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/check/log.log
      exit 1
    fi
    echo "集群状态："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/Number of live region servers/,/Number of regions in transition/p' ${Shell_Path}/log/${Date_Path}/check/log.log
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "正常的表："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/Summary:/,/Status:/p' ${Shell_Path}/log/${Date_Path}/check/log.log | grep 'is okay' | cut -d " " -f 2
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"

    local nu=`grep 'inconsistencies detected' ${Shell_Path}/log/${Date_Path}/check/log.log | cut -d " " -f 1`
    if [ ${nu} != 0 ];then
      echo "检查结果：检测出有"$nu"个表不一致，请查看日志文件："${Shell_Path}/log/${Date_Path}/check/log.log
      echo "环境检查end..."
      exit 1
    else
      echo "检查结果：没有不一致的表"
    fi
    echo "环境检查end..."
}
# -all 不导出hbase命名空间的表
function exportTableInfo() {
    echo "导出表信息start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/tabInfo

    echo "list_namespace" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/tabInfo/namespace.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "源端hbase:list_namespace命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/tabInfo/namespace.log
      exit 1
    fi

    sed -n '/NAMESPACE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/tabInfo/namespace.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/tabInfo/all_namespace.txt

    echo -n "" > ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt
    for ns in `cat ${Shell_Path}/log/${Date_Path}/tabInfo/all_namespace.txt`
    do
      if [ "hbase" = ${ns} ];then
        continue
      fi
       echo "list_namespace_tables '$ns'" |  $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/tabInfo/tables.log 2>&1
       status=$?
       if [ ${status} != 0 ];then
         echo "源端hbase:list_namespace_tables '$ns'命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/tabInfo/tables.log
         exit 1
       fi
       for tb in `sed -n '/TABLE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/tabInfo/tables.log | sed -e '1d' -e '$d'`
       do
         echo $ns":"$tb>>${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt
       done
    done
    cat ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt

    echo "导出的表信息(不包含namespace为hbase的表)，请查看文件"${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt
    echo "导出表信息end..."
}

# -all  目标端不能有重复的表与相同的备份
function snapshot() {
    echo "数据备份start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/snapshot
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/log.log
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/allTable.txt
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_exits.txt
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/dst_table_exits.txt

    ##start 检查目标端是否含有相同的表及相同的备份,有就跳过,不迁移
    echo -n "" > ${Shell_Path}/log/${Date_Path}/snapshot/allTable.txt
    echo "list_namespace" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/namespace.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list_namespace命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/namespace.log
      exit 1
    fi

    sed -n '/NAMESPACE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/namespace.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/snapshot/all_namespace.txt

    for ns in `cat ${Shell_Path}/log/${Date_Path}/snapshot/all_namespace.txt`
    do
      if [ "hbase" = ${ns} ];then
        continue
      fi
       echo "list_namespace_tables '${ns}'" |  $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/tables.log 2>&1
       status=$?
       if [ ${status} != 0 ];then
         echo "目标端hbase:list_namespace_tables '${ns}'命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/tables.log
         exit 1
       fi
       for tb in `sed -n '/TABLE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/tables.log | sed -e '1d' -e '$d'`
       do
         echo $ns":"$tb>>${Shell_Path}/log/${Date_Path}/snapshot/allTable.txt
       done
    done

    #与${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt做对比,判断是否有重复的表名
    for sn in `cat  ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt`
      do
        grep -x $sn ${Shell_Path}/log/${Date_Path}/snapshot/allTable.txt >> ${Shell_Path}/log/${Date_Path}/snapshot/dst_table_exits.txt
      done

    local nu=`cat ${Shell_Path}/log/${Date_Path}/snapshot/dst_table_exits.txt | wc -l`
    if [ ${nu} != 0 ];then
      #echo "目标端已经存在的表,将跳过已存在表的数据迁移，表名如下："
      echo "目标端已经存在的表,请处理后再进行数据迁移，表名如下："
      cat ${Shell_Path}/log/${Date_Path}/snapshot/dst_table_exits.txt
      exit 1
    else
      echo "目标端不存在要迁移的表，将全部迁移..."
    fi

    ##需要迁移的表,删除已存在的表
    #grep -v -f ${Shell_Path}/log/${Date_Path}/snapshot/dst_table_exits.txt ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt > ${Shell_Path}/log/${Date_Path}/snapshot/snapshotTable.txt
    ##end
    cat  ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt>${Shell_Path}/log/${Date_Path}/snapshot/snapshotTable.txt

    ##构建要做的备份名称
    for tb in `cat ${Shell_Path}/log/${Date_Path}/snapshot/snapshotTable.txt`
      do
        OLD_IFS="$IFS"
        IFS=":"
        arr=($tb)
        IFS="$OLD_IFS"
        echo ${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}>>${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt
      done
    echo "要做的备份名称，如下："
    cat ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt

    ##start 检查源端是否含有相同的备份,有就报错,打印日志
    echo "list_snapshots '${snapshot_prefix}qianyi-snap-.*'" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "源端hbase:list_snapshots,命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log
      exit 1
    fi

    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log | sed -e '1d' -e '$d' | cut -d " " -f 2`
    do
      grep -x $sn ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt >> ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt
    done

    local nu=`cat ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt | wc -l`
    if [ ${nu} != 0 ];then
      echo "源端已经存在的snapshot,如下："
      cat ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt
      echo "请删除源端已存在的snapshot 或者 重新设置备份名称前缀"
      exit 1
    else
      echo "源端不存在要创建的snapshot..."
    fi
    ##end

    ##start 检查目标端是否含有相同的备份,有就报错,打印日志
    echo "list_snapshots '${snapshot_prefix}qianyi-snap-.*'" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_list.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list_snapshots,命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_list.log
      exit 1
    fi

    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_list.log | sed -e '1d' -e '$d' | cut -d " " -f 2`
    do
      grep -x $sn ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt >> ${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_exits.txt
    done

    local nu=`cat ${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_exits.txt | wc -l`
    if [ ${nu} != 0 ];then
      echo "目标端已经存在的snapshot,如下："
      cat ${Shell_Path}/log/${Date_Path}/snapshot/dst_snap_exits.txt
      echo "请删除目标端已存在的snapshot 或者 重新设置备份名称前缀"
      exit 1
    else
      echo "目标端不存在要创建的snapshot..."
    fi
    ##end

    #创建snapshot,使用源端的keytab
    for tb in `cat ${Shell_Path}/log/${Date_Path}/snapshot/snapshotTable.txt`
    do
      OLD_IFS="$IFS"
      IFS=":"
      local arr=($tb)
      IFS="$OLD_IFS"
      ##echo "snapshot '$tb','qianyi-snap-${arr[0]}-${arr[1]}'" | hbase shell -n 1>>${Shell_Path}/log/${Date_Path}/snapshot/log.txt 2>&1
      echo "表"$tb",创建snapshot："${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}"开始。。。"
      echo "表"$tb",创建snapshot："${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}"的日志如下：" >> ${Shell_Path}/log/${Date_Path}/snapshot/log.log
      $HBASE_SHELL --config ${src_hbase_conf} snapshot create -t $tb -n ${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]} 1>>${Shell_Path}/log/${Date_Path}/snapshot/log.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "表"$tb",创建snapshot："${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}"失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/log.log
        exit 1
      else
        echo "表"$tb",创建snapshot："${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}"完成。。。"
      fi
    done

    echo "list_snapshots '${snapshot_prefix}qianyi-snap-.*'" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt 2>&1
    status=$?
    if [ ${status} != 0 ];then
      echo "源端hbase:list_snapshots,命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt
      exit 1
    fi

    echo "表的备份信息："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "数据备份end..."
}

# -all
function transfer() {
    echo "数据迁移start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/transfer
    echo -n "" > ${Shell_Path}/log/${Date_Path}/transfer/export_sn.log
    echo -n "" > ${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log
    local status=0

    ##从配置文件获取hbase.rootdir的配置
    hbaseRootDir=`xmllint --xpath '//property[name="hbase.rootdir"]/value/text()' ${dst_hbase_conf}/hbase-site.xml`

    ##start 判断目标端是否已有namespace,没有就需要创建
    sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt | sed -e '1d' -e '$d' | cut -d " " -f 3 > ${Shell_Path}/log/${Date_Path}/transfer/sanpshot_table.txt
    ##需要目标端存在的namespace(去掉default,并进行去重)
    grep : ${Shell_Path}/log/${Date_Path}/transfer/sanpshot_table.txt | cut -d ':' -f 1 | grep -v -x 'default' | sort| uniq > ${Shell_Path}/log/${Date_Path}/transfer/ns.txt

    echo "list_namespace" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/transfer/dst_namespace.log 2>&1
    status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list_namespace命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/dst_namespace.log
      exit 1
    fi
    ##目标端的全部namespace
    sed -n '/NAMESPACE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/dst_namespace.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/transfer/dst_all_namespace.txt

    echo -n "" > ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt
    for ns in `cat ${Shell_Path}/log/${Date_Path}/transfer/ns.txt`
    do
      local nu=`grep -x ${ns} ${Shell_Path}/log/${Date_Path}/transfer/dst_all_namespace.txt | wc -l`
      if [ ${nu} == 0 ];then
        echo ${ns} >> {Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt
      fi
    done

    local nu=`cat {Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt | wc -l`
    if [ ${nu} != 0 ];then
      echo "目标端hbase不存在namespace,需要创建namespace，如下："
      cat {Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt

      echo "目标端创建namespace开始。。。"
      for ns in `cat {Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt`
      do
        echo "创建snapshot："${ns}",开始"
        echo "创建snapshot："${ns}",的日志如下：">>${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log
        echo "create_namespace '${ns}'" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>>${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log 2>&1
        status=$?
        if [ ${status} != 0 ];then
          echo "目标端hbase:create_namespace '${ns}' 命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log
          exit 1
        fi
        echo "创建snapshot："${ns}",结束"
      done
      echo "目标端创建namespace完成。。。"
    else
      echo "目标端hbase已有迁移所需的所有namespace"
    fi
    ##end 判断目标端是否已有namespace,没有就需要创建

    ##迁移snapshot,用hbase1的认证，用hbase的也行，但是要有提交yarn任务的权限，以及hbase1的对应hadoop的路径的权限
    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt | sed -e '1d' -e '$d' | cut -d " " -f 2`
    do
      echo "迁移snapshot:"${sn}",开始。。。"
      echo "迁移snapshot:"${sn}",日志如下：" >> ${Shell_Path}/log/${Date_Path}/transfer/export_sn.log
      $HBASE_SHELL --config ${src_hbase_conf} -Dhdp.version=2.3.7.0-1 org.apache.hadoop.hbase.snapshot.ExportSnapshot -snapshot $sn -copy-to ${dst_hadoop_uris}${hbaseRootDir} 1>>${Shell_Path}/log/${Date_Path}/transfer/export_sn.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "迁移snapshot:${sn},执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/export_sn.log
        exit 1
      fi
      echo "迁移snapshot:"${sn}",完成。。。"
    done

    ##restore_snapshot
    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/snapInfo.txt | sed -e '1d' -e '$d' | cut -d " " -f 2`
    do
      echo "restore_snapshot:"$sn",开始。。。"
      echo "restore_snapshot:"$sn",日志如下：" >> ${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log
      echo "restore_snapshot '${sn}'" | $HBASE_SHELL --config ${dst_hbase_conf}  shell -n 1>>${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "restore_snapshot:${sn},执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log
        exit 1
      fi
      echo "restore_snapshot:"$sn",结束。。。"
    done
    echo "数据迁移end..."
    if [ ${is_data_check} = "true" ];then
      table_file=${Shell_Path}/log/${Date_Path}/snapshot/snapshotTable.txt
      dataCheck
    fi
}

# -ex 不导出hbase命名空间的表
function exportTableInfo1() {
    echo "导出表信息start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/tabInfo

    echo "list" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/tabInfo/tables.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "源端hbase:list命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/tabInfo/tables.log
      exit 1
    fi

    sed -n '/TABLE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/tabInfo/tables.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt
    cat ${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt

    echo "导出的表信息(不包含namespace为hbase的表)，请查看文件"${Shell_Path}/log/${Date_Path}/tabInfo/allTable.txt
    echo "导出表信息end..."
}

# -sn  源端不能有重名的备份
function snapshot1() {
    echo "数据备份start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/snapshot

    cat ${table_file} | grep '^hbase:' > ${Shell_Path}/log/${Date_Path}/snapshot/hbase_table.log
    local nu=`cat ${Shell_Path}/log/${Date_Path}/snapshot/hbase_table.log | wc -l`
    if [ ${nu} != 0 ];then
       echo "提供的文件里面含有hbase命名空间的表,如下，请删除后再进行数据迁移。。。"
       cat ${Shell_Path}/log/${Date_Path}/snapshot/hbase_table.log
       exit 1
    fi

    ##构建要做的备份名称
    for tb in `cat ${table_file}`
      do
        OLD_IFS="$IFS"
        IFS=":"
        local arr=($tb)
        IFS="$OLD_IFS"
        if [ "${#arr[@]}" -eq 1 ];then
          echo ${snapshot_prefix}qianyi-snap-${arr[0]}>>${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt
        else
          echo ${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}>>${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt
        fi
      done
    echo "要做的备份名称，如下："
    cat ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt

    ##start 检查源端是否含有相同的备份,有就报错,打印日志
    echo "list_snapshots '${snapshot_prefix}qianyi-snap-.*'" | $HBASE_SHELL  --config ${src_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "源端hbase:list_snapshots,命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log
      exit 1
    fi

    for sn in `sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/snapshot/source_snap_list.log | sed -e '1d' -e '$d' | cut -d " " -f 2`
    do
      grep -x $sn ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_name.txt >> ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt
    done

    if [ -f ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt ];then
      local nu=`cat ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt | wc -l`
      if [ ${nu} != 0 ];then
        echo "源端已经存在的snapshot,如下："
        cat ${Shell_Path}/log/${Date_Path}/snapshot/src_snap_exits.txt
        echo "请删除源端已存在的snapshot 或者 重新设置备份名称前缀"
        exit 1
      else
        echo "源端不存在要创建的snapshot..."
      fi
    else
      echo "源端不存在要创建的snapshot..."
    fi
    ##end

    #创建snapshot,使用源端的keytab
    for tb in `cat ${table_file}`
    do
      OLD_IFS="$IFS"
      IFS=":"
      local arr=($tb)
      IFS="$OLD_IFS"
      if [ "${#arr[@]}" -eq 1 ];then
        local s_n=${snapshot_prefix}qianyi-snap-${arr[0]}
      else
        local s_n=${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}
      fi
      echo "表"$tb",创建snapshot："${s_n}"开始。。。"
      echo "表"$tb",创建snapshot："${s_n}"的日志如下：" >> ${Shell_Path}/log/${Date_Path}/snapshot/log.log
      $HBASE_SHELL --config ${src_hbase_conf} snapshot create -t $tb -n ${s_n} 1>>${Shell_Path}/log/${Date_Path}/snapshot/log.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "表"$tb",创建snapshot："${s_n}"失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/snapshot/log.log
        exit 1
      else
        echo ${s_n}" "${tb}>>${Shell_Path}/log/${Date_Path}/snapshot/snapshot_table_info.txt
        echo "表"$tb",创建snapshot："${snapshot_prefix}qianyi-snap-${arr[0]}-${arr[1]}"完成。。。"
      fi
    done

    echo "表的备份信息："
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    cat ${Shell_Path}/log/${Date_Path}/snapshot/snapshot_table_info.txt
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "备份信息可查看文件："${Shell_Path}/log/${Date_Path}/snapshot/snapshot_table_info.txt
    echo "数据备份end..."
}

# -tr
function transfer1() {
    echo "数据迁移start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/transfer

    ##校验是否含有hbase命名空间的表
    ##获取表信息(去掉default命令空间)
    sed -e 's/^[\t ]*//g' -e 's/[\t ]*$//g' -e 's/[\t ]\+/ /g' ${snapshot_table_file} | cut -d ' ' -f 2 | sed  "s/^default://g" > ${Shell_Path}/log/${Date_Path}/transfer/table.log
    cat ${Shell_Path}/log/${Date_Path}/transfer/table.log | grep '^hbase:' > ${Shell_Path}/log/${Date_Path}/transfer/hbase_table.log
    local nu=`cat ${Shell_Path}/log/${Date_Path}/transfer/hbase_table.log | wc -l`
    if [ ${nu} != 0 ];then
       echo "提供的文件里面含有hbase命名空间的表,如下，请删除后再进行数据迁移。。。"
       cat ${Shell_Path}/log/${Date_Path}/transfer/hbase_table.log
       exit 1
    fi

    ##start 检查目标端是否含有相同的表,有就提示,停止迁移
    echo "list" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/transfer/dst_table.log 2>&1
    local status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/dst_table.log
      exit 1
    fi

    sed -n '/TABLE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/transfer/dst_table.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/transfer/dst_all_table.log

    grep -f ${Shell_Path}/log/${Date_Path}/transfer/table.log ${Shell_Path}/log/${Date_Path}/transfer/dst_all_table.log > ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_table.log

    nu=`cat ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_table.log | wc -l`
    if [ ${nu} != 0 ];then
      echo "目标端已经存在的表,请处理后再进行数据迁移，表名如下："
      cat ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_table.log
      exit 1
    else
      echo "目标端不存在要迁移的表，可全部迁移..."
    fi
    ##end

    ##start 检查目标端是否含有相同的备份,有就提示,停止迁移
    echo "list_snapshots" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/transfer/dst_snapshots.log 2>&1
    status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list_snapshots命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/dst_snapshots.log
      exit 1
    fi

    sed -n '/SNAPSHOT/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/transfer/dst_snapshots.log | sed -e '1d' -e '$d' | cut -d " " -f 2 > ${Shell_Path}/log/${Date_Path}/transfer/dst_all_snapshots.log
    sed -e 's/^[\t ]*//g' -e 's/[\t ]*$//g' -e 's/[\t ]\+/ /g' ${snapshot_table_file} | cut -d ' ' -f 1 > ${Shell_Path}/log/${Date_Path}/transfer/snapshot.log
    grep -f ${Shell_Path}/log/${Date_Path}/transfer/snapshot.log ${Shell_Path}/log/${Date_Path}/transfer/dst_all_snapshots.log > ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_snapshot.log

    nu=`cat ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_snapshot.log | wc -l`
    if [ ${nu} != 0 ];then
      echo "目标端已经存在的snapshot,请处理后再进行数据迁移，snapshot名如下："
      cat ${Shell_Path}/log/${Date_Path}/transfer/dst_exits_snapshot.log
    else
      echo "目标端不存在要迁移的snapshot，可全部迁移..."
    fi
    ##end

    ##start 判断目标端是否已有namespace,没有就需要创建
    ##需要目标端存在的namespace(已经了去掉'default:')
    grep : ${Shell_Path}/log/${Date_Path}/transfer/table.log | cut -d ':' -f 1 | sort| uniq > ${Shell_Path}/log/${Date_Path}/transfer/ns.txt

    echo "list_namespace" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>${Shell_Path}/log/${Date_Path}/transfer/dst_namespace.log 2>&1
    status=$?
    if [ ${status} != 0 ];then
      echo "目标端hbase:list_namespace命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/dst_namespace.log
      exit 1
    fi
    ##目标端的全部namespace
    sed -n '/NAMESPACE/,/row(s)/p'  ${Shell_Path}/log/${Date_Path}/transfer/dst_namespace.log | sed -e '1d' -e '$d' > ${Shell_Path}/log/${Date_Path}/transfer/dst_all_namespace.txt

    echo -n "" > ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt
    for ns in `cat ${Shell_Path}/log/${Date_Path}/transfer/ns.txt`
    do
      local nu=`grep -x ${ns} ${Shell_Path}/log/${Date_Path}/transfer/dst_all_namespace.txt | wc -l`
      if [ ${nu} == 0 ];then
        echo ${ns} >> ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt
      fi
    done

    local nu=`cat ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt | wc -l`
    if [ ${nu} != 0 ];then
      echo "目标端hbase不存在namespace,需要创建namespace，如下："
      cat ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt

      echo "目标端创建namespace开始。。。"
      for ns in `cat ${Shell_Path}/log/${Date_Path}/transfer/dst_no_exist_namespace.txt`
      do
        echo "创建snapshot："${ns}",开始"
        echo "创建snapshot："${ns}",的日志如下：">>${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log
        echo "create_namespace '${ns}'" | $HBASE_SHELL  --config ${dst_hbase_conf} shell -n 1>>${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log 2>&1
        status=$?
        if [ ${status} != 0 ];then
          echo "目标端hbase:create_namespace '${ns}' 命令执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/create_namespace.log
          exit 1
        fi
        echo "创建snapshot："${ns}",结束"
      done
      echo "目标端创建namespace完成。。。"
    else
      echo "目标端hbase已有迁移所需的所有namespace"
    fi
    ##end 判断目标端是否已有namespace,没有就需要创建

    ##从配置文件获取hbase.rootdir的配置
    hbaseRootDir=`xmllint --xpath '//property[name="hbase.rootdir"]/value/text()' ${dst_hbase_conf}/hbase-site.xml`
    ##迁移snapshot,用hbase1的认证，用hbase的也行，但是要有提交yarn任务的权限，以及hbase1的对应hadoop的路径的权限
    for sn in `cat  ${Shell_Path}/log/${Date_Path}/transfer/snapshot.log`
    do
      echo "迁移snapshot:"${sn}",开始。。。"
      echo "迁移snapshot:"${sn}",日志如下：" >> ${Shell_Path}/log/${Date_Path}/transfer/export_sn.log
      $HBASE_SHELL --config ${src_hbase_conf} -Dhdp.version=2.3.7.0-1 org.apache.hadoop.hbase.snapshot.ExportSnapshot -snapshot $sn -copy-to ${dst_hadoop_uris}${hbaseRootDir} 1>>${Shell_Path}/log/${Date_Path}/transfer/export_sn.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "迁移snapshot:${sn},执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/export_sn.log
        exit 1
      fi
      echo "迁移snapshot:"${sn}",完成。。。"
    done

    ##restore_snapshot
    for sn in `cat  ${Shell_Path}/log/${Date_Path}/transfer/snapshot.log`
    do
      echo "restore_snapshot:"$sn",开始。。。"
      echo "restore_snapshot:"$sn",日志如下：" >> ${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log
      echo "restore_snapshot '${sn}'" | $HBASE_SHELL --config ${dst_hbase_conf}  shell -n 1>>${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "restore_snapshot:${sn},执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/transfer/restore_sn.log
        exit 1
      fi
      echo "restore_snapshot:"$sn",结束。。。"
    done
    echo "数据迁移end..."
    if [ ${is_data_check} = "true" ];then
      table_file=${Shell_Path}/log/${Date_Path}/transfer/table.log
      dataCheck
    fi
}

# -dc / -all
function dataCheck() {
    echo "数据校验start..."
    mkdir -p ${Shell_Path}/log/${Date_Path}/dataCheck
    echo -n "" > ${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log
    local status=0

    echo "源端数据查询开始。。。"
    for tb in `cat ${table_file}`
    do
      echo ${tb}",源端数据查询开始。。。"
      echo ${tb}",源端数据查询日志如下:" >> ${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log
      $HBASE_SHELL --config ${src_hbase_conf} -Dhdp.version=2.3.7.0-1 org.apache.hadoop.hbase.mapreduce.RowCounter ${tb} 1>>${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "RowCounter:${tb},源端执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log
        echo ${tb}"=源端数据查询失败" >> ${Shell_Path}/log/${Date_Path}/dataCheck/srcDataQueryResult.txt
        continue
      else
        local rows=`cat ${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log | grep 'ROWS=' | tail -1 | cut -d '=' -f 2`
        echo ${tb}"="${rows} >> ${Shell_Path}/log/${Date_Path}/dataCheck/srcDataQueryResult.txt
      fi
      echo ${tb}",源端数据查询结束。。。"
    done
    echo "源端数据查询全部结束。。。"

    echo "目标端数据查询开始。。。"
    for tb in `cat ${table_file}`
    do
      echo ${tb}",目标端数据查询开始。。。"
      echo ${tb}",目标端数据查询日志如下:" >> ${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log
      $HBASE_SHELL --config ${dst_hbase_conf} -Dhdp.version=2.3.7.0-1 org.apache.hadoop.hbase.mapreduce.RowCounter ${tb} 1>>${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log 2>&1
      status=$?
      if [ ${status} != 0 ];then
        echo "RowCounter:${tb},目标端执行失败，请查看日志文件："${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log
        echo ${tb}"=目标端数据查询失败" >> ${Shell_Path}/log/${Date_Path}/dataCheck/dstDataQueryResult.txt
        continue
      else
        local rows=`cat ${Shell_Path}/log/${Date_Path}/dataCheck/data_check.log | grep 'ROWS=' | tail -1 | cut -d '=' -f 2`
        echo ${tb}"="${rows} >> ${Shell_Path}/log/${Date_Path}/dataCheck/dstDataQueryResult.txt
      fi
      echo ${tb}",目标端数据查询结束。。。"
    done
    echo "目标端数据查询全部结束。。。"

    echo "表名    源端数据条数    目标端数据条数" >> ${Shell_Path}/log/${Date_Path}/dataCheck/dataCheckResult.txt
    for tb in `cat ${table_file}`
    do
      s_nu=`grep ^${tb}= ${Shell_Path}/log/${Date_Path}/dataCheck/srcDataQueryResult.txt | cut -d "=" -f 2 `
      d_nu=`grep ^${tb}= ${Shell_Path}/log/${Date_Path}/dataCheck/dstDataQueryResult.txt | cut -d "=" -f 2 `
      echo ${tb}"  "${s_nu}"  "${d_nu} >> ${Shell_Path}/log/${Date_Path}/dataCheck/dataCheckResult.txt
    done

    echo "数据校验结果，如下：（可查看结果文件："${Shell_Path}/log/${Date_Path}/dataCheck/dataCheckResult.txt"）"
    cat ${Shell_Path}/log/${Date_Path}/dataCheck/dataCheckResult.txt

    echo "数据校验end..."
}

# -h
function printHelp() {
    echo "功能介绍："
    echo "1.输入相应的参数，执行相应的功能："
    echo "  a.状态检查，请输入参数：-sc hbase客户端的根目录 待检测HBASE集群的配置文件目录"
    echo "  b.导出表信息，请输入参数：-ex hbase客户端的根目录 源端hbase的配置文件目录"
    echo "      说明：不导出hbase命名空间的表"
    echo "  c.数据备份，请输入参数：-sn  hbase客户端的根目录 源端hbase的配置文件目录 要备份的表名称列表文件(全路径) 备份名称前缀"
    echo "      说明：要备份的表名称列表文件，内容示例如下(格式：namespaceName:tableName，每一张表一行,默认命名空间default可加可不加)："
    echo "        table1"
    echo "        namespace:table1"
    echo "        namespace:table2"
    echo "        注意：不备份hbase命名空间的表"
    echo "  d.数据迁移，请输入参数：-tr hbase客户端的根目录 源端hbase的配置文件目录 目标端hbase的配置文件目录 目标端hbase对应hadoop集群的访问地址 迁移的表及snapshot的名称列表文件(全路径) 是否进行数据校验"
    echo "      说明：迁移的snapshot的名称列表文件，内容示例如下(格式：snapshotName namespace:tablename，每一个备份名称及表名一行,中间用空格分割，表名称中默认命名空间default可加可不加)："
    echo "        qianyi-snap-default-table1 table1"
    echo "        qianyi-snap-ns1-test1 ns1:test1"
    echo "        qianyi-snap-ns1-test2 ns1:test2"
    echo "        注意：不迁移hbase命名空间的表"
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
    echo "  sh backup.sh -all hbase.root.dir=/usr/hbase src.hbase.conf=/root/hbase/conf dst.hbase.conf=/root/hbase1/conf dst.hadoop.uris=hdfs://hdfs-ha snapshot.prefix=qianyi is.data.check=false"
}

function all(){
  statusCheck
  exportTableInfo
  snapshot
  transfer
}

function checkAllParam() {
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

 if [ "x"${paramMap['dst.hbase.conf']} = "x" ];then
    error_nu=error_nu+1
    echo "请配置目标端hbase的配置文件目录参数：dst.hbase.conf"
  else
    dst_hbase_conf=${paramMap['dst.hbase.conf']}
    if [ ! -d "${dst_hbase_conf}" ];then
      error_nu=error_nu+1
      echo "dst.hbase.conf的配置目录：${dst_hbase_conf}不存在或不是目录，请正确配置"
    fi
  fi

 if [ "x"${paramMap['dst.hadoop.uris']} = "x" ];then
    error_nu=error_nu+1
    echo "目标端hbase对应hadoop集群的访问地址参数：dst.hadoop.uris"
  else
    dst_hadoop_uris=${paramMap['dst.hadoop.uris']}
 fi

 if [ ${error_nu} != 0 ];then
   exit 1
 fi

 if [ "x"${paramMap['snapshot.prefix']} = "x" ];then
     echo "可选参数,备份名称前缀：snapshot.prefix,没有配置"
 else
   snapshot_prefix=${paramMap['snapshot.prefix']}"-"
 fi

 if [ "x"${paramMap['is.data.check']} = "xfalse" ];then
     is_data_check=${paramMap['is.data.check']}
 fi
 echo "是否进行数据校验："${is_data_check}
}

function checkParamSc() {
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

 if [ ${error_nu} != 0 ];then
   exit 1
 fi
}

function checkParamSn() {
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

 if [ "x"${paramMap['snapshot.prefix']} = "x" ];then
     echo "可选参数,备份名称前缀：snapshot.prefix,没有配置"
 else
   snapshot_prefix=${paramMap['snapshot.prefix']}"-"
 fi
}

function checkParamTr() {
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

 if [ "x"${paramMap['dst.hbase.conf']} = "x" ];then
    error_nu=error_nu+1
    echo "请配置目标端hbase的配置文件目录参数：dst.hbase.conf"
  else
    dst_hbase_conf=${paramMap['dst.hbase.conf']}
    if [ ! -d "${dst_hbase_conf}" ];then
      error_nu=error_nu+1
      echo "dst.hbase.conf的配置目录：${dst_hbase_conf}不存在或不是目录，请正确配置"
    fi
  fi

 if [ "x"${paramMap['dst.hadoop.uris']} = "x" ];then
    error_nu=error_nu+1
    echo "目标端hbase对应hadoop集群的访问地址参数：dst.hadoop.uris"
  else
    dst_hadoop_uris=${paramMap['dst.hadoop.uris']}
 fi

 if [ "x"${paramMap['snapshot.table.file']} = "x" ];then
    error_nu=error_nu+1
    echo "请配置要校验的表名称列表文件(全路径)：snapshot.table.file"
  else
    snapshot_table_file=${paramMap['snapshot.table.file']}
    if [ ! -f "${snapshot_table_file}" ];then
      error_nu=error_nu+1
      echo "snapshot.table.file的配置：${snapshot_table_file}不存在或不是文件，请正确配置"
    fi
  fi

 if [ ${error_nu} != 0 ];then
   exit 1
 fi

  if [ "x"${paramMap['is.data.check']} = "xfalse" ];then
    is_data_check=${paramMap['is.data.check']}
  fi
  echo "是否进行数据校验："${is_data_check}
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
dst_hbase_conf=""
dst_hadoop_uris=""
snapshot_prefix=""
table_file=""
snapshot_table_file=""
is_data_check="true"

#根据操作类型，选择执行的流程
if [ $# != 0 ]
then
  echo "操作类型：$1"
  op_type=$1
  if [ "-sc" = ${op_type} ];then
    parseParam "$@"
    checkParamSc
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    statusCheck
  elif [ "-ex" = ${op_type} ];then
    parseParam "$@"
    checkParamSc
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    exportTableInfo1
  elif [ "-sn" = ${op_type} ];then
    parseParam "$@"
    checkParamSn
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    snapshot1
  elif [ "-tr" = ${op_type} ];then
    parseParam "$@"
    checkParamTr
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    transfer1
  elif [ "-all" = ${op_type} ];then
    parseParam "$@"
    checkAllParam
    echo "本次执行结果目录为："${Shell_Path}/log/${Date_Path}
    all
  elif [ "-h" = ${op_type} ];then
    printHelp
  else
    echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
  fi
else
   echo "请输入正确的参数，输入-h 查看相应的功能参数信息"
fi
