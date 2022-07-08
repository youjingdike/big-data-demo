package com.collection.tojava

import java.util
import scala.collection.{JavaConversions, JavaConverters}
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object CollectionTst {

  /*
  * scala的集合 -scala.collection
  *            -scala.collection.immutable
  *            -scala.collection.mutable
  */

  def main(args: Array[String]): Unit = {
    val scalaImmutableMap: Map[Any, Any] = Map("1" -> "tst", "2" -> "tst1");
    val scalaMutableMap: scala.collection.mutable.Map[Any, Any] = scala.collection.mutable.Map("1" -> "tst", "2" -> "tst1");
    val scalaMutableMap1: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map("1" -> "tst", "2" -> "tst1");

    val javaMap: util.Map[Object, Object] = new util.HashMap(scalaImmutableMap.asJava.asInstanceOf[util.Map[Object, Object]])
    javaMap.put("3", "3")
    javaMap.asScala.foreach(println(_))

    println("@@@@@@@@1")

    val javaMap1: util.Map[String, String] = new util.HashMap(scalaImmutableMap.map(x => x._1.toString -> x._2.toString).asJava)
    javaMap1.put("4", "4")
    javaMap1.asScala.foreach(println(_))
    println("@@@@@@@@2")

    //该方式不能对map做修改，可使用第一种方式转换
    val javaMap2: util.Map[Object, Object] = scalaImmutableMap.asJava.asInstanceOf[util.Map[Object, Object]]
    //javaMap2.put("5","5")
    javaMap2.asScala.foreach(println(_))

    println("@@@@@@@@3")
    //该方式不能对map做修改，可使用第一种方式转换
    val javaMap3 = JavaConverters.mapAsJavaMapConverter(scalaImmutableMap).asJava
    //javaMap3.put("6","6")
    javaMap3.asScala.foreach(println(_))

    println("@@@@@@@@4")
    //    val tuples= JavaConversions.mapAsJavaMap(scalaImmutableMap)
//        val tuples= JavaConversions.mapAsJavaMap(scalaMutableMap)
//    val tuples: util.Map[Any, Any] = JavaConversions.mutableMapAsJavaMap(scalaMutableMap)
    val tuples: util.Map[String, String] = JavaConversions.mutableMapAsJavaMap(scalaMutableMap1)
    tuples.put("3","3")
    tuples.asScala.foreach(println(_))

    println("@@@@@@@@41")
    val databaseList = new ArrayBuffer[String]
    databaseList.add("t1")
    databaseList.add("t2")
    //如果要调用变长参数的方法，要用如下方法传参：databaseList:_*
    database(databaseList:_*)

    println("@@@@@@@@5")
    val javaMapTst: java.util.Map[String,String] = new util.HashMap()
    javaMapTst.put("1","1")
    javaMapTst.put("2","2")
    //
    val asScala:scala.collection.mutable.Map[String, String] = javaMapTst.asScala
    asScala.put("3","3")
    asScala.foreach(println(_))
    println("@@@@@@@@6")
    val scalaMap: scala.collection.mutable.Map[String, String] = JavaConversions.mapAsScalaMap(javaMapTst)
    scalaMap.put("4","4")
    scalaMap.foreach(println(_))

    println("@@@@@@@@7")
    val mutableScalaMap:scala.collection.mutable.Map[String,String] = scala.collection.mutable.Map()
    val immutableMap:Map[String,String] = Map("a"->"a")
    mutableScalaMap ++= immutableMap
    mutableScalaMap.foreach(println(_))

  }

  //变长参数
  def database(databaseList: String*) = {
      databaseList.foreach(println(_))
    }
}
