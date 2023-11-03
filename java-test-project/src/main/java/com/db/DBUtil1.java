package com.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBUtil1 {
        static Properties properties = null;
        public static Properties getDBInfo(){
//          InputStream is=DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("db1.properties");
            
            //如果位于包下面的资源。则可以
//          InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("com/bjsxt/jdbc/db.properties");
            properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return properties;
        }
        
        public static Connection getConn(){
            Connection conn = null;
            
            if(properties == null){
                getDBInfo();
            }
            
            try {
                /*Class.forName(properties.getProperty("driver"));
                String url=properties.getProperty("url"); //orcl为数据库的SID 
                String user=properties.getProperty("user"); 
                String password=properties.getProperty("password");  
                conn = DriverManager.getConnection(url,user,password);*/
                
                Class.forName(properties.getProperty("driver")).newInstance(); 
                String url=properties.getProperty("url");
                String user=properties.getProperty("user"); 
                String password=properties.getProperty("password"); 
                conn= DriverManager.getConnection(url,user,password);  

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return conn;
        }
        
        public static void close(ResultSet rs,Statement stmt, Connection conn){
            try {
                if(rs!=null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(stmt!=null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(conn!=null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        public static void close(Statement stmt, Connection conn){
            try {
                if(stmt!=null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(conn!=null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
}
