/**
 * ThreadLocal线程局部变量
 *
 * ThreadLocal是使用空间换时间，synchronized是使用时间换空间
 * 比如在hibernate中session就存在与ThreadLocal中，避免synchronized的使用
 *
 * 运行下面的程序，理解ThreadLocal
 *
 */
package com.thread.concurrent.yxxy.c_022;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ThreadLocal3 {
	private final static String DB_URL = "";
	private static ThreadLocal<Connection> connHolder = new ThreadLocal<Connection>(){
		@Override
		protected Connection initialValue() {
			try {
				return DriverManager.getConnection(DB_URL);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	public static Connection getCon() {
		return connHolder.get();
	}
}


