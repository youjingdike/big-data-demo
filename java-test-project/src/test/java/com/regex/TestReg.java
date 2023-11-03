package com.regex;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xingqian
 * @测试正则表达式
 */
public class TestReg {
    /**
     * 验证传入的字符串是否整个匹配正则表达式
     * @param regex 正则表达式
     * @param decStr 要匹配的字符串
     * @return 若匹配，则返回true;否则，返回false;
     */
    public static boolean validataAll(String regex, String decStr) {
        //表达式对象
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        //创建Matcher对象
        Matcher m = p.matcher(decStr);
        //是否完全匹配
        boolean b = m.matches();//该方法尝试将整个输入序列与该模式匹配
        return b;
    }
    
    /**
     * 验证传入的字符串是否有子字符串匹配正则表达式
     * @param regex 正则表达式
     * @param decStr 要匹配的字符串
     * @return 若匹配，则返回true;否则，返回false;
     */
    public static boolean validataSub(String regex, String decStr) {
        //表达式对象
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        //创建Matcher对象
        Matcher m = p.matcher(decStr);
        //是否完全匹配
        boolean b = m.find();//该方法扫描输入序列以查找与该模式匹配的下一个子序列。
        return b;
    }
    
    /**
     * 给定字符串中是否有符合给定正则表达式的子字符串，返回匹配的第一个子字符串
     * @param regex：正则表达式
     * @param decStr：要匹配的字符串
     * @return :返回匹配的第一个字符串，若不匹配则null
     */
    public static String search(String regex, String decStr) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(decStr);
        System.out.println("匹配到的数量："+m.groupCount());
      //是否完全匹配
        //boolean found = m.find();//该方法扫描输入序列以查找与该模式匹配的下一个子序列。
        String foundStr = "";
        try {
            
            /*if (found) {
                foundStr = m.group();
                String foundstring0 = m.group(0);  //group(),group(0)返回符合整个表达式的子字符串
                String foundstring1 = m.group(1);  //group(1)返回符合整个表达式的子字符串中匹配第一个表达式的子字符串
                String foundstring2 = m.group(2);   //group(2)返回符合整个表达式的子字符串中匹配第二个表达式的子字符串
                String foundstring3 = m.group(3);
                System.out.println("foundstring:"+foundStr);
                System.out.println("foundstring0:"+foundstring0);
                System.out.println("foundstring1:"+foundstring1);
                System.out.println("foundstring2:"+foundstring2);
                System.out.println("foundstring3:"+foundstring3);
            }*/
            while (m.find()) {
            	int groupCount = m.groupCount();
            	System.out.println("groupCount:"+groupCount);
                String foundstring0 = m.group(0);  //group(),group(0)返回符合整个表达式的子字符串
                String foundstring1 = m.group(1);  //group(1)返回符合整个表达式的子字符串中匹配第一个表达式的子字符串
                System.out.println("foundstring:"+foundStr);
                System.out.println("foundstring0:"+foundstring0);
                System.out.println("foundstring1:"+foundstring1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundStr;
    }
    
    /**
     * 返回给定字符串中匹配给定正则表达式所有子字符串
     * @param regex
     * @param decStr
     * @return List：返回所有匹配正则表达式的子字符串
     */
    public static List<String> searchSubStr(String regex,String decStr) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(decStr);  
        List<String> list = new ArrayList<String>();
        while(m.find()){
            list.add(m.group());
        }
        for (String string : list) {
System.out.println(string); 
        }
        return list;
    }

    public static Set<String> searchSubStr1(String regex,String decStr) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(decStr);
        Set<String> set = new HashSet<String>();
        while(m.find()){
            set.add(m.group());
        }
        for (String string : set) {
            System.out.println(string);
        }
        return set;
    }

    /**
     * 替换给定字符串中匹配正则表达式的子字符串
     * @param regex：正则表达式
     * @param decStr：所要匹配的字符串
     * @param replaceStr：将符合正则表达式的子串替换为该字符串
     * @return：返回替换以后新的字符串
     */
    public static String replace(String regex,String decStr,String replaceStr) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(decStr);
        // 替换
        String newstring = m.replaceAll(replaceStr);
System.out.println(newstring);
        return newstring;
    }

    public static void testSplit(String str,String regex) {
        // 分割
        String [] strs = str.split(regex);
        for(int i=0;i<strs.length;i++) {
            System.out.println(i+":::::::"+strs[i]);
        }
    }   
    
    @Test
    public void testValidataAll() {
        System.out.println(validataAll("\\w+京", "334455aaa京"));
        System.out.println(validataAll("\\d+", "334455aaa"));
    }
    
    @Test
    public void testValidataSub() {
        System.out.println(validataSub("\\d+", "334455aaa"));
        System.out.println(validataSub("\\d+", "hhhhhaaa"));
    }
    
    @Test
    public void testSearch() {
//        search("(\\d+)([a-z]+)(\\d+)", "334455aaa33--3232423bbb22-3232ccc411-3232ddd411"); 
    	String message = "";
    	message = "=====================>> 【1】【2】 返回给微信信息: <xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        search("(【)(.{1})(】)", message); 
    }
    
    @Test
    public void testSearchSub() {
//        System.out.println(searchSubStr("(\\d+)([a-z]+)", "334455aaa--3232423aaa-32324bbb"));
        System.out.println(searchSubStr("(\\d+)([a-z]+)", "${dt1}-${dt2}-${dt3}-ddd 00:00:${dt4}"));
    }
    
    @Test
    public void testReplace() {
        replace("\\d", "dsfd;sa;ksd12a34b567c890d888e999f","*");
    }
    
    @Test
    public void testSp() {
//        testSplit("abc5Adefghi7Ajklmn","(\\d)A");
    	String log = "2017-12-21 15:57:06.754 [catalina-exec-5] - [INFO]     com.tzx.cc.service.rest.OrderRest(:81)     [ORDER -\u003e update]总部收到请求数据:{\"code\":0,\"data\":[{\"bill_num\":\"\",\"chanel\":\"WM10\",\"dispatch_time\":null,\"distribution_time\":null,\"finish_time\":null,\"order_code\":\"\",\"order_repayment\":[],\"order_state\":\"\",\"order_type\":\"OUTSIDE_ORDER\",\"receive_time\":\"\",\"receive_time_cancellation\":\"\",\"receive_time_dispatch\":null,\"receive_time_distribution\":null,\"receive_time_finish\":null,\"receive_time_qd\":\"2016-12-28 15:50:57\",\"take_time\":\"2016-12-28 15:50:57\",\"third_order_code\":\" \"}],\"msg\":\"\",\"oper\":\"update\",\"pagination\":{\"asc\":false,\"orderby\":\"\",\"pageno\":0,\"pagesize\":0,\"totalcount\":0},\"secret\":\"\",\"source\":\"SERVER\",\"store_id\":4,\"success\":true,\"t\":1513843172196,\"tenancy_id\":\"yunnby\",\"type\":\"ORDER\"}";
        testSplit(log,"\\s+");
    }

    public static List<String> getSubUtil(String soap,String rgex){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    @Test
    public void tst(){
//        System.out.println(getSubUtil("${dt1}-${dt2}-${dt3}-ddd 00:00:${dt4}","(\\$\\{)(.*?)\\}"));
        searchSubStr1("(?<=(\\$\\{))(.*?)(?=\\})","${dt1}ddddd44444444-${dt2}-${dt3}-ddd 00:00:${dt4}");
    }


}
