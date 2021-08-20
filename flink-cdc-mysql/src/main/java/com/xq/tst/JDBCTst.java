package com.xq.tst;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.sql.*;

public class JDBCTst {
    public static void main(String[] args) throws Exception {
        try{
//            String driver="com.mysql.jdbc.Driver";       //1.定义驱动程序名为driver内容为com.mysql.jdbc.Driver
            String driver="com.mysql.cj.jdbc.Driver";       //1.定义驱动程序名为driver内容为com.mysql.jdbc.Driver
            String url="jdbc:mysql://localhost:3306/test?"   //2.定义url；jdbc是协议；mysql是子协议：表示数据库系统管理名称；localhost：3306是你数据库来源的地址和目标端口；test是我本人建的表位置所在处，你以你的为标准。
                    + "useUnicode=true&characterEncoding=UTF8"; //防止乱码；useUnicode=true表示使用Unicode字符集；characterEncoding=UTF8表示使用UTF-8来编辑的。
            String user="cdc";                  //3.定义用户名，写你想要连接到的用户。
            String pass="cdc";                 //4.用户密码。
            String querySql="select * from cdc";     //5.你想要查找的表名。
            Class.forName(driver);               //6.注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以 Class.forName("com.mysql.jdbc.Driver");
            Connection conn= DriverManager.getConnection(url,user,pass);//7.获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成
            //括号里面的url，user，pass便是前面定义的2,3,4步骤内容；
            Statement stmt=conn.createStatement();  //8.构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
            //  三种实现方法分别为：Statement stmt = con.createStatement() ;
            //          PreparedStatement pstmt = conn.prepareStatement(sql) ;
            //          CallableStatement cstmt = conn.prepareCall("{CALL demoSp(? , ?)}") ;
            ResultSet rs=stmt.executeQuery(querySql);//9.执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。
            while(rs.next()){  //10.遍历结果集
                System.out.println("人员编号:"+rs.getInt("id")+"工资:"+rs.getString("name")+"姓名:"+rs.getInt("age"));//使用getString()方法获取你表里的资料名
            }
            if(rs !=null){//11.关闭记录集
                try{
                    rs.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(stmt !=null){//12.关闭声明的对象
                try{
                    stmt.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(conn !=null){//13.关闭连接 （记住一定要先关闭前面的11.12.然后在关闭连接，就像关门一样，先关里面的，最后关最外面的）
                try{
                    conn.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
