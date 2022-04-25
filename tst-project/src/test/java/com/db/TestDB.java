package com.db;

import com.db.DBUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TestDB {
    
    @Test
    public void test() {
        
        String string = "2012-05-06";
        String sql = "select count(*) from iacmain where inputdate > '" + string + "'";
        ResultSet rs = null;
        Connection  conn = DBUtil.getConn();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        int i = 0;
        try {
            while(rs.next()){
                i = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(i);
    }

    @Test
    public void test1() {
//          InputStream is=DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka.properties");

            //如果位于包下面的资源。则可以
//          InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("com/bjsxt/jdbc/db.properties");
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

        System.out.println(properties.getProperty("s"));
    }

}
