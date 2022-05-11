///*
//package com.xq.tst.processors
//
//import com.alibaba.fastjson.JSON
//import org.apache.flink.api.java.typeutils.RowTypeInfo
//import org.apache.flink.configuration.Configuration
//import org.apache.flink.connectors.hive.HiveOptions
//import org.apache.flink.connectors.hive.util.JobConfUtils
//import org.apache.flink.streaming.api.scala.DataStream
//import org.apache.flink.table.catalog.{CatalogTable, ObjectIdentifier}
//import org.apache.flink.types.Row
//import org.apache.hadoop.hive.conf.HiveConf
//import org.apache.hadoop.mapred.JobConf
//
//import java.nio.file.Paths
//import java.util
//import scala.collection.JavaConverters._
//import scala.collection.mutable
//
//@Service("HiveSink")
//class HiveSinkProcessor(name: String, childs: mutable.MutableList[Processor[DataStream[Row]]], configs: Map[Any, Any], sm: StreamingMate)
//  extends TableOperateProcessor(name, childs, configs, sm) {
//
//  val dbName = configs("database").toString
//  val tblName = configs("table").toString
//  val confPath = configs("configPath").toString
//  val isStrictCheck = configs.getOrElse("isStrictCheck", "false").toString.toBoolean
//  val notNullSchema = configs("notNullSchema").toString
//  val schema = configs("schema").toString
//  val hiveVersion = configs.getOrElse("hive.version", "3.1.2").toString
//  val partition = configs.getOrElse("assignerColumn", "").toString
//  val autoCompaction = configs.getOrElse("smallFileCompaction", "false").toString.toBoolean
//  val compactionFilesize = configs.getOrElse("smallFileSize", "128").toString
//  val tableExecHiveFallbackMapredWriter = configs.getOrElse(HiveOptions.TABLE_EXEC_HIVE_FALLBACK_MAPRED_WRITER, "false").toString.toBoolean
//
//  override def innerBuild(dataStream: DataStream[Row], dataStream2: DataStream[Row]): DataStream[Row] = {
//    val rowType = dataStream.getType.asInstanceOf[RowTypeInfo]
//    val metadataRowTypeInfo: RowTypeInfo = JsonRowSchemaConverter.convert(schema).asInstanceOf[RowTypeInfo]
//
//    // 校验字段 个数、名称、类型
//    val diff: Boolean = MetadataCheck.checkJDBCFieldType(rowType, metadataRowTypeInfo, isStrictCheck, notNullSchema)
//    if (!diff) {
//      throw new RuntimeException("元数据校验异常：上游字段个数、名称、类型与数据管理定义字段不一致。")
//    }
//    val configMap: util.Map[Object, Object] = new util.HashMap(configs.asJava.asInstanceOf[util.Map[Object, Object]])
//    // 并行度设置
//    var parallelism: String = configs.getOrElse("parallelism", dataStream.getParallelism.toString).toString
//    if (parallelism.isEmpty) parallelism = dataStream.getParallelism.toString
//
//    val options = new util.HashMap[String, String]()
//    if (autoCompaction) {
//      options.put("auto-compaction", autoCompaction.toString)
//      options.put("compaction.file-size", compactionFilesize+"MB")
//    }
//
//    val partitions = if (partition.equals("")) null else partition.split(",")
//    val fieldNames = rowType.getFieldNames
//    val dataTypes = DataTypeUtils.ex(rowType)
//    val rowDataStream = dataStream.javaStream.flatMap(new RowDataMapper(dataTypes, metadataRowTypeInfo, configMap))
//
//    HiveConf.setHiveSiteLocation(Paths.get(confPath, "hive-site.xml").toUri().toURL())
//    val hiveConf = new HiveConf()
//    val jobConf: JobConf = JobConfUtils.createJobConfWithCredentials(hiveConf);
//    jobConf.set("hive.version", hiveVersion)
//    val identifier: ObjectIdentifier = ObjectIdentifier.of("HiveCatalog", dbName, tblName)
//    val table: CatalogTable = new CatalogTableBuilder()
//      .setProps(options)
//      .setDataTypes(DataTypeUtils.generateDataType(rowType, JSON.parseObject(schema).getJSONObject("properties"), JSON.parseObject(notNullSchema)))
//      .setFieldNames(fieldNames)
//      .setPrimaryKeys(null)
//      .setPartitionKeys(partitions)
//      .setTypeIn(rowType.getFieldTypes)
//      .builder()
//
//
//    val flinkConf: Configuration = new Configuration()
//    flinkConf.setBoolean(HiveOptions.TABLE_EXEC_HIVE_FALLBACK_MAPRED_WRITER,tableExecHiveFallbackMapredWriter)
//    val hiveSink: HiveSink = new HiveSink(flinkConf, jobConf, identifier, table, parallelism.toInt,
//      configMap, metadataRowTypeInfo)
//    hiveSink.consume(rowDataStream)
//
//    dataStream
//  }
//
//}*/
