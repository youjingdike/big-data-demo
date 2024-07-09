package com.junit;

import com.util.StringUtils;
import com.vo.Man;
import com.vo.Woman;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class TestUseJunit {
    
    private static String asti = "ab";
    
    //在编译时调用该语句块
    /*static {
        System.out.println("nihao:我是static");
    }*/
    //在每次调用构造器之前调用该语句块
    /*{
        System.out.println("nihao:我不是static");
    }*/
    public static void main(String[] args) {
/*
        System.out.println("sssssssssssssssss" + new Man("x").getClass());
        int i = 0;
        for (;i<5;i++) {
            System.out.println(i);
        }
        System.out.println("i = " + i);
        --i;
        System.out.println(i);
        assert i == 5;
*/

        TreeMap<Long, String> treeMap = new TreeMap<>();
        treeMap.put(1L,"1L");
        treeMap.put(2L,"2L");
        treeMap.put(3L,"3L");
        treeMap.put(4L,"4L");
        treeMap.put(5L,"5L");

        NavigableMap<Long, String> headMap = treeMap.headMap(3L, true);
        headMap.forEach((aLong, s) -> System.out.println(s));
        headMap.clear();
        System.out.println("@@@@@@@@@");
        treeMap.forEach(((aLong, s) -> System.out.println(s)));
    }
    
    //调用这个方法即可传多个参数，也可传数组
    void testArgs(int... is) {
        for (int i : is) {
            System.out.println(i);
        }
    }
    
    //调用这个方法必须用数组
    void testArgs2(int[] is) {
        for (int i : is) {
            System.out.println(i);
        }
    }
    
    @Test
    public void testMethodArg() {
        new TestUseJunit().testArgs(5,5,5,5,0);
        System.out.println("****************");
        //testArgs2不能这样传参
        //new TestUseJunit().testArgs2(5,5,5,5,0);
        new TestUseJunit().testArgs(new int[]{1,2,3,4,5,6,8});
        System.out.println("****************");
        new TestUseJunit().testArgs2(new int[]{1,2,3,4,5,6,8});
    }
    
    @Test
    public void testInterger() {
        //(自动装箱拆箱时，对于-128-127之间的值,编译器仍然会把它当做基本类型处理。)
        //按对象类型处理
        Integer s = new Integer(9);
        Integer t = new Integer(9);
        Integer ii = 255;
        Integer jj = 255;
        //按基础类型处理
        int i = new Integer(2551);
        int j = new Integer(2551);
        Integer ss = 9;
        Integer tttt = 9;
        System.out.println(s == t);       //false
        System.out.println(s.equals(t));  //true
        System.out.println(i == j);       //true
        System.out.println(ss == tttt);   //true
        System.out.println(ii == jj);     //false
        
        double d = 0/500.0;
        System.out.println(d);
        System.out.println((int)Math.ceil(d));
        
        Double dou = 0.99;
        int dd = dou.intValue();
        double ddd = dou.doubleValue();
        int in = (int)ddd;
        System.out.println(dd);
        System.out.println(in);
        
        double nan = Double.MAX_VALUE;
        System.out.println("NaN:" + nan);
        Boolean boolean1 = Boolean.valueOf("true");
        System.out.println(boolean1.booleanValue());
        
        BigDecimal bgBigDecimal = new BigDecimal(12);
        bgBigDecimal = bgBigDecimal.add(new BigDecimal(5));
        System.out.println(bgBigDecimal.intValue());
        int iiii = 017;//这是8进制数
        System.out.println(iiii);
        System.out.println("suspicious: \12"+"8");
        
        System.out.println(Math.ceil(-12.9));
        System.out.println(Math.floor(-12.2));
        System.out.println(Math.round(12.4));
        long iMax = Integer.MAX_VALUE;
        int iMax2 = Integer.MAX_VALUE;
        long sum = iMax + iMax2;
        System.out.println(iMax);
        System.out.println(sum);
        short s1 = 1;
        short s2 = 1;
//        s1 = s1 + 1; //错误
        s1 = (short)(s1 + 1);
        s2 += 1;
        char c = '\u0061';
        char cc = '\u0041';
        int dd1 = 65;
        char ddd1 = (char)dd1;
        System.out.println(cc);
        System.out.println(dd1);
        System.out.println(ddd1);
        System.out.println("s1:" + s1);
        System.out.println("s2:" + s2);
        
        int i2 = 5;
        int j2 = 0;
        int k2 = 0;
        j2 = i2++;
        k2 = ++i2;
        System.out.println(i2 + ":" + j2 + ":" + k2);
    }
    
    @Test
    public void testDouble() {
        Double v = 0.0;
        System.out.println(Double.isNaN(v));
    }
    
    @Test
    public void testBigDecimal() {
        double d1 = 2.13;
        DecimalFormat df = new DecimalFormat("0.00");  //这个不是四舍五入
        System.out.println(df.format(d1));
        
        System.out.println(1234567.0);
        System.out.println(12345678.0); //double类型超过7为就转换为科学技术法
        
        System.out.println(new BigDecimal(0.1));  
        System.out.println(new BigDecimal("0.1")); //要精确计算就用这个
        
        double d2 = 1.01;
        double d3 = d1 + d2;
        System.out.println("d3:" + d3);
        
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        double d4 = b1.add(b2).doubleValue();
        System.out.println("d4 :" + d4);
        
        int i = 1;
        int j = BigDecimal.ROUND_HALF_UP; //舍入的模式
//        int j = BigDecimal.ROUND_UP;
        double d5 = b1.add(b2).setScale(i, j).doubleValue();
        System.out.println("d5:" + d5);
        
        
    }
    
    @Test
    public void getIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } 
        String ip = addr.getHostAddress().toString(); 
        System.out.println("ip:" + ip);
    }
    
    @Test
    public void getProp() {
        Properties prop = new Properties();
        String endTime = "";
//        InputStream in = TestUseJunit.class.getClassLoader().getResourceAsStream("sinocitxconf.properties");
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("sinocitxconf.properties");
        try {
            prop.load(in);
            endTime = new String(prop.getProperty("endtime").getBytes("ISO-8859-1"),"UTF-8") ;
        } catch (IOException e1) {
            return;
        }
        System.out.println(endTime);
        SimpleDateFormat sim = new SimpleDateFormat("HH");
        Date d = new Date();
        String end = sim.format(d);
        System.out.println(end);
        if (!endTime.equals(end)) {
            System.out.println("不相等");
        } else {
            System.out.println("相等");
        }
    }
     
    @Test
    public void testCollection() {
        Collection<Integer> c=new ArrayList<Integer>();
        Integer obj=new Integer(1);
        Integer obj1=new Integer(1);
        c.add(obj);
        c.add(obj1);
        Integer[] b=(Integer [])c.toArray(new Integer[c.size()]);
//        Integer[] b=(Integer [])c.toArray(new Integer[1]);
        for (Integer integer : b) {
            System.out.println(integer);
        }
        
        
        String ss = (String)null;
        List list = new ArrayList();
        list.add(ss);
//      list.add(null);
//      list.add(null);
//      list.add(null);
//      list.add(null);
//      list.add(null);
        list.remove(null);      //只删除第一个null
        System.out.println(list.get(0));
        System.out.println(list.size());
        
        List list2 = new ArrayList();
        add(list2);
        for (int i = 0; i < list2.size(); i++) {
            System.out.println(list2.get(i));
        }
    }
    
    static void add (List list) {
        list.add("1");
        list.add("2");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }
    
    @Test
    public void testEqualsAndHaseCode() {
        //****************测试equals和hashCode方法的重写（下列情况要重写，并且两个方法都重写）
        //1.放入Collection容器中的自定义对象，调用remove,contains等方法；
        //2.要将我们自定义的对象放入HashSet中处理；
        //3.要将我们自定义的对象作为HashMap的key处理；
        Map m = new HashMap();
        Man man = new Man("xing");
        Man man1 = new Man("xing");
        Man man2 = new Man("xingqian");
        m.put(man, "0");
        m.put(man1, "1");
        m.put(man2, "2");
        System.out.println("man:" + m.size());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        for (Object obj : m.entrySet()) {
            Entry em = (Entry) obj;
            System.out.println(em.getKey());
            System.out.println(em.getValue());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        Man man3 = new Man("xing");
        m.remove(man3);
        for (Object obj : m.entrySet()) {
            Entry em = (Entry) obj;
            System.out.println(em.getKey());
            System.out.println(em.getValue());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(man.equals(man1));
        Set s = new HashSet();
        s.add(man1);
        s.add(man);
        for (Object o : s) {
            if (o instanceof Man) {
                System.out.println("man.name:" + ((Man)o).getName());
            }
        }
        Map mm = new HashMap();
        Woman wan = new Woman("xing");
        Woman wan1 = new Woman("xing");
        Woman wan2 = new Woman("xingqian");
        mm.put(wan, "0");
        mm.put(wan1, "1");
        mm.put(wan2, "2");
        System.out.println("women:" + mm.size());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        for (Object obj : mm.entrySet()) {
            Entry em = (Entry) obj;
            System.out.println(em.getKey());
            System.out.println(em.getValue());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        Woman wan3 = new Woman("xing");
        m.remove(wan3);
        for (Object obj : mm.entrySet()) {
            Entry em = (Entry) obj;
            System.out.println(em.getKey());
            System.out.println(em.getValue());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(wan.equals(wan1));
        Set ws = new HashSet();
        ws.add(wan1);
        ws.add(wan);
        for (Object o : ws) {
            if (o instanceof Woman) {
                System.out.println("women.name:" + ((Woman)o).getName());
            }
        }
    }
    
    @Test
    public void testMap() {
        Map<String, String> map = new Hashtable<String, String>();
        map.put("1", "nihao");
        map.put("2", "haljflsak");
        
        //*****************遍历map的常用方法
        Set<Entry<String,String>> set = map.entrySet();
        Iterator<Entry<String, String>> entry = set.iterator();
        while(entry.hasNext()) { 
            Entry<String, String> en = entry.next();
            System.out.println(en.getValue());
        }
        
        System.out.println("******************");
        for (Object obj : map.entrySet()) {
            Entry<String, String> em = (Entry<String, String>) obj;
            System.out.println(em.getKey());
            System.out.println(em.getValue());
        }
        //******************************************* 
        
        System.out.println("******************");
        System.out.println(map.remove("5"));
        System.out.println(map.get("1"));
        System.out.println(map.get("2"));
        System.out.println(map.get("3"));
        System.out.println(map.get("4"));
    }
    
    @Test
    public void testDateAndTime() {
        Date daaa = new Date();
        SimpleDateFormat s = new  SimpleDateFormat("yyyy-MM-dd");
        System.out.println(s.format(daaa));
        System.out.println(daaa);
        System.out.println(daaa.getDate());
        System.out.println(daaa.getDay());
        System.out.println(daaa.getYear());
        
        
        /*DateTime dt = new DateTime(new Date());
        DateTime dt1 = new DateTime();
        DateTime d = new DateTime("");
        System.out.println("d:"+d);
        System.out.println("dt:"+dt);
        System.out.println("dt1:"+dt1);*/
    }
    
    @Test
    public void testStringAndBufferAndBuilder() {
        StringBuilder s = new StringBuilder();
        s.append("'");
        s.append("");
        s.append("'");
        System.out.println(s.toString());
        
        //String ss = (String)null; //这种写法有什么意义？
        
        String st = "1";
        System.out.println(!(st == null || "".equals(st)));
        System.out.println(st != null && !"".equals(st));
        if (st != null && !"".equals(st)) {
            System.out.println("w b s kong");
        }
        
        String strr = "sfasdfdsf";
        System.out.println("1" + strr.substring(0, strr.length()));
        System.out.println("2" + strr.substring(0, strr.length()-1));
        System.out.println("3" + strr.substring(0, 9));
        System.out.println("4" + strr.substring(0, 8));
        System.out.println("5" + strr.substring(0, 0));
        System.out.println("6" + strr.substring(9, 9));
        //System.out.println("7" + strr.substring(10, 10));
        
        String strg = "" + "1";
        String s12 = "  " + "1";
        try {
            System.out.println("1-----"+strg.substring(0, 1));
//            System.out.println("2-----"+strg.substring(1, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(" 长度-----"+s12.length());
            System.out.println("3-----"+s12.substring(0, 1));
            System.out.println("4-----"+s12.substring(1, 2));
            System.out.println("5-----"+s12.substring(2, 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("6-----"+s12.substring(1, 1));
        
        String string = "          ";
        if (StringUtils.isEmptyString(string)) {
            System.out.println("wo shi kong");
        } else {
            System.out.println("wo bu shi kong");
        }
        
        if ("".equals(string.trim())) {
            System.out.println("wo shi kong");
        } else {
            System.out.println("wo bu shi kong");
        }
        
        String str1 = "abc";  
        String str2 = "abc";  
        System.out.println(str1 == str2);//true  
        //可以看出str1和str2是指向同一个对象的。  
        //Java代码  
        String str1n = new String ("abc");  
        String str2n = new String ("abc");  
        System.out.println(str1n == str2n);  //false
        //用new的方式是生成不同的对象,每一次生成一个。  

        String s0 = "kvill";  
        String s1 = "kvill";  
        String s2 = "kv" + "ill";  //在编译时会进行优化，去掉加号直接编译成一个字符串，这时缓冲区有，就直接拿
        System.out.println(s0==s1); //true   
        System.out.println(s0==s2);//true  

        String ss0 = "kvill";  
        String ss1 = new String("kvill");  
        String ss2 = "kv" + new String("ill");  
        System.out.println( ss0==ss1 );  //false
        System.out.println( ss0==ss2 );  //false
        System.out.println( ss1==ss2 );  //false

        String st0 = "kvill";  
        String st1 = new String("kvill");  
        String st2 = new String("kvill");  
        System.out.println( st0 == st1 );//false  
        st1.intern();  
        st2 = st2.intern(); //把常量池中"kvill"的引用赋给s2   
        System.out.println( st0==st1);  //false
        System.out.println( st0==st1.intern() );//true  
        System.out.println( st0==st2 );//true
        System.out.println("*****************");
        
        String ts1 = new String("kvill");
        String ts2 = ts1.intern();
        System.out.println( ts1 == ts1.intern() );//false
        System.out.println( ts2 == ts1.intern() );//true

        String a = "a1";
        String b = "a" + 1; //在编译时会进行优化，去掉加号直接编译成一个字符串，这时缓冲区有，就直接拿
        System.out.println((a == b)); //true
        String as ="atrue";
        String bs = "a" + "true";  //在编译时会进行优化，去掉加号直接编译成一个字符串，这时缓冲区有，就直接拿
        System.out.println((as == bs));//true 
        String ast ="a3.4";
        String bst = "a" + 3.4; //在编译时会进行优化，去掉加号直接编译成一个字符串，这时缓冲区有，就直接拿
        System.out.println((ast == bst)); //true
        String bss = "a" + 3.4; //在编译时会进行优化，去掉加号直接编译成一个字符串，这时缓冲区有，就直接拿
        String ass ="a3.4";
        System.out.println((ass == bss)); //true
        
        String str11 = "ab";
        String str22 = "b";
        String str = "a" + str22;
        System.out.println((str11 == str)); //false

        String af = "ab";
        final String bb = "b";
        String bf = "a" + bb;
        System.out.println((af == bf)); //true

        String ad = "ab";
        final String bbd = getBB();
        String bd = "a" + bbd;
        System.out.println((ad == bd)); //false

        String sd1 = "a";  
        String sd2 = "b";  
        String sd = sd1 + sd2;  
        System.out.println(sd == asti); //false   
        System.out.println(sd.intern() == asti);//true   

    }
    
    static String getBB() {
        return "b";
    }
    
    @Test
    public void testClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader1 = TestUseJunit.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(classLoader.getParent());
        System.out.println(classLoader.getParent().getParent());
        System.out.println(classLoader1);
        System.out.println(classLoader1.getParent());
        System.out.println(classLoader1.getParent().getParent());
        System.out.println(new TestUseJunit().getClass().getClassLoader().toString());
    }
    
    @Test
    public void testFileInputStream() {
        /*try {
            FileInputStream  fis = new  FileInputStream("d:/a.txt");  //内容是：abc
            int s1 = fis.read();  //97
            int s2 = fis.read();   //98
            int s3 = fis.read();   //99
            int s4 = fis.read();   //-1
            int s5 = fis.read();   //-1
            System.out.println(s1);
            System.out.println(s2);
            System.out.println(s3);
            System.out.println(s4);
            System.out.println(s5);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        //上面代码升级为如下代码：
        /*FileInputStream  fis =null;
        try {
            fis = new  FileInputStream("d:/a.txt");  //内容是：abc
            StringBuilder sb = new StringBuilder();
            int temp = 0;
            while((temp=fis.read())!=-1){
                sb.append((char)temp);
            }
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                if(fis!=null)
                    fis.close();   //流对象，使用完后必须关闭！！
            }catch(IOException e){
                e.printStackTrace();
            }
        }*/
        
        //升级为:
        /*FileInputStream fis = null;
        try {
            fis  = new FileInputStream("d:/a.txt");
            StringBuilder  sb = new StringBuilder();
            int temp = 0;
            byte[]  buf = new byte[1024];
            
            while((temp=fis.read(buf))!=-1) {    //  !=-1
                String str= new String(buf,0,temp);
                sb.append(str);
//                System.out.println(temp);
//                System.out.println(str);
            }
            System.out.print(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) { 
            e.printStackTrace();
        } finally {
            try {
                if(fis!=null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/       
        
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        int temp = 0;
        
        long date1 = System.currentTimeMillis();
        try {
            fis = new FileInputStream("d:/a.txt");  //内容是：abc
             
            /*while((temp = fis.read())!=-1){
                System.out.println(temp);
                sb.append((char)temp);
            }*/
            
            //使用数组作为缓存，读取的效率大大提高
            byte[] buffer = new byte[1024];   
            while((temp=fis.read(buffer))!=-1) {
                sb.append(new String(buffer,0,temp));
            }
            
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File("d:"+File.separator+"a.txt");
        }
        long date2 = System.currentTimeMillis();
        System.out.println("耗时："+(date2-date1));
    }
    
    
    @Test
    public void testFileOutputStream() {
        
        FileOutputStream fos = null;
        String string = "开发商附件 sdas暗示法大师傅飞";
        try {
            fos = new FileOutputStream("d:/out.txt",true);
            fos.write(string.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    
    @Test
    public void testCopyFile() {
        copyFile("d:/a.txt", "d:/b.txt");
        copyFileByBuffered("d:/Excel公式.pptx", "d:/bbb.pptx");
    }
    
    @Test
    public void testCopy() {
        File file = new File("D:\\MyDrivers");
        File file2 = new File("D:/实验copy");
        copyDir(file,file2);
    }
    
    //负责copy一个目录下面所有的文件和子目录到另一个目录
    static void copyDir(File file,File file2) {
        boolean is = new File(file2,file.getName()).mkdir();
        System.out.println(is);
        File[] files = file.listFiles();
        for(File f:files) {
            if(f.isDirectory()) {
                copyDir(f,new File(file2,file.getName()));
            }
            if(f.isFile()) {
                copyFile(f,new File(new File(file2,file.getName()),f.getName())); 
            }
        }
    }

    static void copyFile(File src,File dec){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];  //为了提高效率，设置缓存数组
        int temp = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dec);
            while((temp=fis.read(buffer))!=-1){
                fos.write(buffer, 0, temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    static void copyFile(String src,String dec) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dec);
            byte[] buffer = new byte[1024];
            int temp = 0;
            while ((temp=fis.read(buffer)) != -1) {
                fos.write(buffer, 0, temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    static void copyFileByBuffered(String src,String dec) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int temp = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dec);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);
            //无需使用缓存数组
            while((temp=bis.read())!=-1){
                bos.write(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //增加处理流后，注意流的关闭顺序！“后开的先关闭！”
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testByteArrayInputStream() {
        ByteArrayInputStream bais = null;
        StringBuilder sb = new StringBuilder();
        int temp = 0;
        int num = 0;
        long date1 = System.currentTimeMillis();
        try {
            byte[] b = "abcdefghijklmnopqrstuvwxyz".getBytes();
//          bais = new ByteArrayInputStream(b);  
            bais = new ByteArrayInputStream(b,2,10);  
             
            while((temp = bais.read())!=-1) {
                sb.append((char)temp);
                num++;
            }

            System.out.println(sb);
            System.out.println("读取的字节数："+num); 
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long date2 = System.currentTimeMillis();
        System.out.println("耗时："+(date2-date1));

    }
    
    @Test
    public void testByteArrayOutputStream() {
        FileInputStream fis = null;
        byte[] byteArray = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream("d:/a.txt");
            byteArray = getByteArrayFromFis(fis);
            for (byte b : byteArray) {
                sb.append((char)b);
//                System.out.println((char)b);
            }
            System.out.println(sb.toString());
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    static byte[] getByteArrayFromFis(FileInputStream fis) {
        ByteArrayOutputStream baos = null;
        int temp = 0;
        try {
            baos = new ByteArrayOutputStream();
            
            while ((temp = fis.read()) != -1) {
                baos.write(temp);
            }
            
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Test
    public void testDataStream() {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(
                    (new FileOutputStream("./aa.txt"))));
            dis = new DataInputStream(new BufferedInputStream(
                    new FileInputStream("./aa.txt")));
                    
            dos.writeDouble(Math.random());
            dos.writeBoolean(true);
            dos.writeInt(10);
            dos.writeInt(11);
            dos.writeInt(12);
            dos.writeChar('a');
            dos.writeUTF("中國字");
            dos.flush();
            
            
            //从文件中直接读取数据
            System.out.println("double: " + dis.readDouble());
            System.out.println("boolean: " + dis.readBoolean());
            System.out.println("int: " + dis.readInt());
            System.out.println("int: " + dis.readInt());
            System.out.println("int: " + dis.readInt());
            System.out.println("char: " + dis.readChar());
            System.out.println("String: " + dis.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testPrintStream() {
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(new File("d:" 
                    + File.separator + "test.txt")));
            ps.print("nihao");
            ps.println("你好");
            ps.println("你好");
            ps.print("nihao");
            ps.print("nihao");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            ps.close();
        }
    }
    
    @Test
    public void testFileReaderAndWriter() {
        FileReader fr = null;
        FileWriter fw = null;
        int c = 0;
        try {
            fr = new FileReader("d:/a.txt");
            fw = new FileWriter("d:/d.txt");
            
            while((c=fr.read())!=-1){
                fw.write(c);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testBufferedReaderAndWriter() {
        FileReader fr = null;
        FileWriter fw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        String tempString = "";
        try {
            fr = new FileReader("d:/a.txt");
            fw = new FileWriter("d:/db.txt");
            br = new BufferedReader(fr);
            bw = new BufferedWriter(fw);
            
            while((tempString=br.readLine())!=null){
                bw.write(tempString);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testGBK() {
        String str = "打发第三方";
        byte[] bt = null;
        try {
            bt = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (byte b : bt) {
            System.out.println(b);
        }
    }
    
    @Test
    public void testTrimGBK() {
        String str = "大发发送asdsa功夫撒旦法";
        byte[] buffer = null;
        try {
            buffer = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = trimGBK(buffer, 14);
        System.out.println(length);
        String subStr = str.substring(0, length);
        System.out.println(subStr);
    }
    
    /**
     * 按输入的字符个数输出包含汉字的字符串，但是不能输出半个汉字
     * @param buffer
     * @param n
     * @return
     */
    static int trimGBK(byte[] buffer,int n) {
        int num = 0;
        boolean isChineseFirstHalf = false;
        for (int i=0; i<n; i++) {
            if (buffer[i] < 0 && !isChineseFirstHalf) {
                isChineseFirstHalf = true;
            } else {
                num++;
                isChineseFirstHalf = false;
            }
        }
        return num;
    }
    
    @Test
    public void testArrays() {
        int[][] ar = new int[3][] ;
        //a[0] = {1,2,5};   //错误，没有声明类型就初始化
        ar[0] = new int[]{1,2};
        ar[1] = new int[]{2,2};
        ar[2] = new int[]{2,2,3,4};
        System.out.println(ar[2][3]);

        
        int[] a = {1,2,3,4,5};
        int[] b = {6,7,8,9,10};
        List<int[]> list = new ArrayList<int[]>();
        List<Integer> list1 = new ArrayList<Integer>();
        list = Arrays.asList(a);
        list = Arrays.asList(b);
        list = Arrays.asList(a,b);
        list1 = Arrays.asList(1,2,3,4,5);
        System.out.println("woshi:a:" + list.get(0)[4]);
        System.out.println("woshi:b:" + list.get(1)[4]);
        System.out.println("woshi:" + list1.get(0));
        System.out.println("woshi:" + list1.get(1));
        System.out.println("woshi:" + list1.get(2));
    }
    
    @Test
    public void testSanMu() {
        int i = 8;
        int is = i>7?1:i>2?2:3;//三目运算符从左往右判断
        System.out.println(is);
    }
    
    @Test
    public void testConvert() {
        System.out.println(convert(1011));
    }
    
    static String convert(int money) {
        char[] data = {'零','壹','贰','叁','肆','伍','陆','柒','扒','玖'};
        char[] units = {'元','拾','佰','仟','万','拾','佰','仟','亿','拾'};
        StringBuffer sb = new StringBuffer();
        int unit = 0;
        while (money != 0) {
            sb.insert(0, units[unit++]);
            int number = money % 10;
            sb.insert(0, data[number]);
            money /= 10;
        }
        return sb.toString().replaceAll("零[拾佰仟]", "零").replaceAll("零+万", "万")
                .replaceAll("零+元", "元").replaceAll("零+亿", "亿").replaceAll("零+", "零").replaceAll("亿万", "亿");
    }
    
    @Test
    public void testTime() {
        System.out.println(new Date());
    }
}
