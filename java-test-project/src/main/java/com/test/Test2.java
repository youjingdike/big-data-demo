package com.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Test2 {
	public static void main(String[] args) {
		/*FileOutputStream fos = null;
        String string = "saf dasf adf adf afd";
        boolean flag = true;
        int i = 0;
        while(flag){
        	i++;
        	if (i==200) {
        		flag = false;
        	}
	        try {
	        		fos = new FileOutputStream("d:/cs/out"+i+".txt",true);
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
        }*/
//		testJson();
		/*int i = 5;
		int addtime = 0;
		*//**（通知频率为15/15/30/180/300/300/480/600/600，单位：秒） 共计42分钟*//*
		switch (i){
			
			case 1:
			case 2:
				addtime = 15*1000;
				break;
			case 3:
				addtime = 30*1000;
				break;	
			case 4:
				addtime = 180*1000;
				break;	
			case 5:
			case 6:
				addtime = 300*1000;
				break;
			case 7:
				addtime = 480*1000;
				break;
			case 8:
			case 9:
				addtime = 600*1000;
				break;
			default:
				break;
		}
		System.out.println(addtime);
		try {
			SECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		/*System.out.println(testReturn());
		System.out.println(testReturn1());*/

		String s = "123{K8S_TEST}456";
		String replace = s.replace("{K8S_TEST1}", null);
		System.out.println(replace);

	}

	private static int testReturn() {
		try {
			return 1;
		} finally {
			return 2;
		}
	}

	private static int testReturn1() {
		int i = 0;
		try {
			return i;
		} finally {
			i = 2;
			return i;
		}
	}

	public static void testJson() {
		Type type = new TypeToken<ArrayList<JsonObject>>()
        {}.getType();
//        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        
      //拿到本地JSON 并转成String
//        String strByJson = JsonToStringUtil.getStringByJson(this, R.raw.juser_1);
String strByJson = "["
		+ "{\"id\": 2,"
        +"\"usergroup_name\": \"复杂模式测试\","
        +"\"filters\": {\"sql\":\"select distinct guid,idfa,extra['deviceId'] from tracking where year='2017' and month='11' and day='1'  and line='c2c' and tracking_type='pageload' and platform='ios'\"},"
        +"\"line\": \"2\","
        		+"\"is_valid\": 1,"
        		+"\"type\": 2,"
        		+"\"run_status\": 2,"
        		+"\"remark\": \"11月1日 ios活跃用户\","
        		+"\"act_uid\": \"0\","
        		+"\"act_name\": \"\","
        		+"\"usernum\": 0,"
        		+"\"finished_at\": null,"
        		+"\"updatedAt\": \"2017-11-08 14:33:14\","
        		+"\"createdAt\": \"2017-11-08 13:45:13\"},"
    +"{"
    +"\"id\": 4,"
        		+"\"usergroup_name\": \"测试\","
        		+"\"filters\": {\"sql\":\"select * from usertable\"},"
        		+"\"line\": \"2\","
        		+"\"is_valid\": 1,"
        		+"\"type\": 2,"
        		+"\"run_status\": 0,"
        		+"\"remark\": \"\","
        		+"\"act_uid\": \"0\","
        		+"\"act_name\": \"\","
        		+"\"usernum\": 0,"
        		+"\"finished_at\": null,"
        		+"\"updatedAt\": \"2017-11-08 20:14:27\","
        		+"\"createdAt\": \"2017-11-08 20:14:27\""
        +"}"
+"]";
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(strByJson).getAsJsonArray();
System.out.println(jsonArray.size());
System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        //加强for循环遍历JsonArray
        for (JsonElement user : jsonArray) {
            //使用GSON，直接转成Bean对象
        	System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
        	JsonObject asJsonObject = user.getAsJsonObject();
System.out.println("id:"+asJsonObject.get("id"));
System.out.println(asJsonObject.get("filters").getAsJsonObject().get("sql").getAsString());
        }
	}
}
