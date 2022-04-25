/**
 * @FILE: PublicUtil.java
 * @AUTHOR: nieqingyun
 * @DATE: Aug 14, 2013 10:08:17 AM
 **/
package com.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PublicUtil {
	
	public static boolean isEmpty(Object s){
		if(s!=null&&(!s.toString().equals(""))){
			return false;
		}else{
			return true;
		}
	}
	
	/**@description		处理e-plant平台乱码问题
	 * @param str
	 * @return
	 * @author	nieqingyun
	 */
	
	public static String decodeFormStr(String str){
		if(str!=null){
			if(str.indexOf("+")>=0) 
				str = str.replaceAll("\\+"," ");
			if (str.indexOf("%") >= 0)
				try {
					str = URLDecoder.decode(str, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return str;
	}
	
	public static void decodeMap(Map map) throws Exception{
		if (map == null) return; 
		Set keySet = map.keySet();
		for (Object key : keySet) {
			Object value = map.get(key);
			value = URLDecoder.decode(value==null?null:value.toString(), "UTF8");
			map.put(key, value);
		}
	}
	
	public static Map decode(Map map) throws Exception{
		if (map == null)return null; 
		Map resMap = new HashMap();
		Set keySet = map.keySet();
		for (Object key : keySet) {
			Object value = map.get(key);
			value = URLDecoder.decode(value==null?null:value.toString(), "UTF8");
			resMap.put(key, value);
		}
		return resMap;	
	}
	
	public static Timestamp getTimestamp(String a){
		SimpleDateFormat simpledateformat=null;
		if(a.length()>19){
			a=a.substring(0, 19);
		}
		if(a.length()==19){
			simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else if (a.length()==10){
			simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
		}
		Date date=null;
		try {
			date = simpledateformat.parse(a);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new Timestamp(date.getTime());
		
	}
	
	/**
	 * 加载资源文件
	 * @param file
	 * @return
	 */
	public Properties loadProperties(String file)
  {
      Properties p = new Properties();
      InputStream is = getClass().getResourceAsStream((new StringBuilder()).append("/").append(file).toString());
      try
      {
          p.load(is);
      }
      catch(FileNotFoundException e)
      {
          e.printStackTrace();
      }
      catch(IOException e)
      {
          e.printStackTrace();
      }
      return p;
  }
	
	/**
	 * 将byte转换为int
	 * @param b
	 * @return
	 */
	public static int byteToInt (byte b) {
		return  b & 0xff;
	}
}

