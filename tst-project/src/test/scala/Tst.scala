import scala.collection.JavaConverters._
import java.util
import scala.collection.mutable.ArrayBuffer
object Tst {
  def main(args: Array[String]): Unit = {
    val configs: Map[Any, Any] = Map("1"->"tst","2"->"tst1");

    val configMap: util.Map[Object, Object] = new util.HashMap(configs.asJava.asInstanceOf[util.Map[Object, Object]])
    configMap.asScala.foreach(println(_))


    val configMap1: util.Map[String, String] = new util.HashMap(configs.map(x => x._1.toString -> x._2.toString).asJava)
    configMap1.asScala.foreach(println(_))

    val databaseList = new ArrayBuffer[String]
    /*def databaseList(databaseList: String*) = {
      this.configFactory.databaseList(databaseList)
      this
    }*/

    //如果要调用动态参数的方法，要用如下方法传参：databaseList:_*
  }
}
