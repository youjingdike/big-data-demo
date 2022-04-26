import scala.collection.JavaConverters._
import java.util
import scala.collection.mutable.ArrayBuffer
object Tst {
  def main(args: Array[String]): Unit = {
    val configs: Map[Any, Any] = Map("1"->"tst","2"->"tst1");

    val configMap: util.Map[Object, Object] = new util.HashMap(configs.asJava.asInstanceOf[util.Map[Object, Object]])
    configMap.put("3","3")
    configMap.asScala.foreach(println(_))

    println("@@@@@@@@")

    val configMap1: util.Map[String, String] = new util.HashMap(configs.map(x => x._1.toString -> x._2.toString).asJava)
    configMap1.put("4","4")
    configMap1.asScala.foreach(println(_))
    println("@@@@@@@@")

    //该方式不能对map做修改，可使用第一种方式转换
    val configMap2: util.Map[Object, Object] = configs.asJava.asInstanceOf[util.Map[Object, Object]]
    //configMap2.put("5","5")
    configMap2.asScala.foreach(println(_))

    println("@@@@@@@@")
    //该方式不能对map做修改，可使用第一种方式转换
    val configMap3 = mapAsJavaMapConverter(configs).asJava
    //configMap3.put("6","6")
    configMap3.asScala.foreach(println(_))

    val databaseList = new ArrayBuffer[String]
    /*def databaseList(databaseList: String*) = {
      this.configFactory.databaseList(databaseList)
      this
    }*/

    //如果要调用动态参数的方法，要用如下方法传参：databaseList:_*
  }
}
