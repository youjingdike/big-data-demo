package com.collection.toscala;

import org.junit.Test;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import java.util.HashMap;
import java.util.Map;

public class Tst {

    @Test
    public void tst() {
        Map<String,String> javaMapTst = new HashMap();
        javaMapTst.put("1","1");
        javaMapTst.put("2","2");

        //会对javaMapTst进行元素的添加
        scala.collection.mutable.Map<String, String> asScala= JavaConverters.mapAsScalaMapConverter(javaMapTst).asScala();
        asScala.put("3","3");
        Map<String, String> stringStringMap = JavaConverters.mapAsJavaMapConverter(asScala).asJava();
        stringStringMap.forEach((k,v) -> System.out.println(k+":"+v));
        System.out.println("@@@@@@@@");
        stringStringMap.put("t", "t");
        stringStringMap.forEach((k,v) -> System.out.println(k+":"+v));

        System.out.println("@@@@@@@@1");
        scala.collection.mutable.Map<String, String> scalaMap = JavaConversions.mapAsScalaMap(javaMapTst);
        scalaMap.put("4","4");
        JavaConversions.mutableMapAsJavaMap(scalaMap).forEach((s, s2) -> System.out.println(s+":"+s2));
    }
}
