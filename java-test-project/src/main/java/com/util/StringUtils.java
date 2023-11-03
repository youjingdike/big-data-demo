package com.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 字符串工具类
 */
public class StringUtils {
	public static final double MAX_SET_VALUE = 1000000.0D;// 最大值
	public static final double PRE_SET_VALUE = 0.0D;

	/**
	 * 【功能】解析双精度类型的数 <br>
	 * 【规则】<br>
	 * 1、如果为null或trim后长度为0，返回0.0d。 <br>
	 * 2、使用Double.parseDouble()的结果：<br>
	 * 	当返回NaN时，返回0.0d； <br>
	 * 	当返回-0.0d时，返回0.0d（说明，-0.0d是小于0.0d的。） <br>
	 * 	无法解析时，也就是非数字，返回0.0d<br>
	 * 	当过大的时候，比如大于1000000，抛出异常。<br>
	 * 【版本】2007-08-16 乔有良
	 * @param value：传入字符串
	 * @return 双精度数据类型
	 * @throws Exception
	 */
	public static double parseDouble(String value) throws Exception {
		double result = PRE_SET_VALUE;
		try {
			if (value != null && value.length() > 0) {
				result = Double.parseDouble(value);
				if (result == -0.0D) {
					result = PRE_SET_VALUE;
				} else if (Double.isNaN(result)) {// 如果是NaN
					result = PRE_SET_VALUE;
				} else if (result > MAX_SET_VALUE) {// 数值过大，抛出异常
//					throw new UserException(-98, 9201, "StringUtils", "接口数据错误。数值过大。");
				}
			}
		} catch (NumberFormatException e) {// 如果不是数字
			result = PRE_SET_VALUE;
		}
		return result;
	}

	/**
	 * 去掉前后空格
	 * 
	 * @param value
	 *            传入串
	 * @return 返回String
	 */
	public static String trim(String value) {
		String result = value;
		if (value == null) {
			result = "";
		} else {
			result = value.trim();
		}
		return result;
	}

	/**
	 * Trim ,兼容格式用
	 * 
	 * @param value
	 *            byte[]
	 * @return 返回 byte[]
	 */
	public static byte[] trim(byte[] value) {
		byte[] result = value;
		return result;
	}

	/**
	 * 将Null转换为"0"
	 * 
	 * @param value
	 *            传入串
	 * @return 返回String
	 */
	public static String convertNullToZero(String value) {
		String result = value;
		if (value == null || value.trim().equals("")) {
			result = "0";
		}
		return result;
	}

	/**
	 * 将Null转换为""
	 * 
	 * @param source
	 *            传入串
	 * @return 返回String
	 */
	public static String convertNullToZeroString(String source) {
		if (source == null) {
			source = "";
		}
		if (source.equalsIgnoreCase("null")) {
			source = "";
		}
		return source;
	}

	/**
	 * 将字符串的第一个字符小写
	 * 
	 * @param iString
	 *            传入字符串
	 * @return 传出字符串
	 */
	public static String lowerCaseFirstChar(String iString) {
		String newString;
		newString = iString.substring(0, 1).toLowerCase()
				+ iString.substring(1);
		return newString;
	}

	/**
	 * 将字符串的第一个字符大写
	 * 
	 * @param iString
	 *            传入字符串
	 * @return 传出字符串
	 */
	public static String upperCaseFirstChar(String iString) {
		String newString;
		newString = iString.substring(0, 1).toUpperCase()
				+ iString.substring(1);
		return newString;
	}

	/**
	 * 得到短文件名
	 * 
	 * @param fileName
	 *            文件名
	 * @return 短文件名
	 */
	public static String getShortFileName(String fileName) {
		String shortFileName = "";
		int pos = fileName.lastIndexOf('\\');
		if (pos == -1) {
			pos = fileName.lastIndexOf('/');
		}
		if (pos > -1) {
			shortFileName = fileName.substring(pos + 1);
		} else {
			shortFileName = fileName;
		}

		return shortFileName;
	}

	/**
	 * 将字符串按照指定的分隔字符进行拆分,返回拆分后的字符串数组
	 * 
	 * @param originalString
	 *            待拆分的字符串
	 * @param delimiterString
	 *            分隔字符串
	 * @return 字符串数组
	 */
	public static String[] split(String originalString, String delimiterString) {
		int index = 0;
		String[] returnArray = null; // 返回值字符串数组
		int length = 0; // 数组的大小

		// null值校验
		if (originalString == null || delimiterString == null) {
			return null;
		}

		// 空串校验
		if (originalString.equals("") || delimiterString.equals("")
				|| originalString.length() < delimiterString.length()) {
			return new String[] { originalString };
		}

		// 计算字符串有多少个分隔符
		String strTemp = originalString;
		while (strTemp != null && !strTemp.equals("")) {
			index = strTemp.indexOf(delimiterString);
			if (index == -1) {
				break;
			}
			length++;
			strTemp = strTemp.substring(index + delimiterString.length());
		}
		returnArray = new String[++length];

		// 生成字符串数组
		for (int i = 0; i < length - 1; i++) {
			index = originalString.indexOf(delimiterString);
			returnArray[i] = originalString.substring(0, index);
			originalString = originalString.substring(index
					+ delimiterString.length());
		}
		returnArray[length - 1] = originalString;

		return returnArray;
	}

	/**
	 * 得到流的长度
	 * 
	 * @param inputStream
	 *            inputStream
	 * @return 流的长度
	 * @throws Exception
	 */
	public static int getStreamLength(InputStream inputStream) throws Exception {
		int length = 0;
		length = inputStream.available();
		return length;
	}

	public static String getStringFromInputStream(InputStream is)
			throws Exception {
		if (is == null || is.available() < 1) {
			return "";
		}
		// char[] buff = new char[4096];
		// int nch;
		// StringBuffer buffer = new StringBuffer();
		// BufferedReader in = new BufferedReader(new InputStreamReader(is));
		// while((nch = in.read(buff,0,buff.length)) != -1){
		// buffer.append(new String(buff,0,nch));
		// }
		//
		// String tempInfo = "";
		// String s;
		// while((s = in.readLine()) != null){
		// tempInfo += s + System.getProperty("line.separator");
		// }
		// in.close();
		// return buffer.toString();

		byte[] buff = new byte[8192];
		byte[] result = new byte[is.available()];
		int nch;
		BufferedInputStream in = new BufferedInputStream(is);
		int pos = 0;
		while ((nch = in.read(buff, 0, buff.length)) != -1) {
			System.arraycopy(buff, 0, result, pos, nch);
			pos += nch;
		}
		in.close();
		return new String(result);
	}

	/**
	 * 从流中得到字符数组
	 * 
	 * @param is
	 *            InputStream
	 * @return字符数组
	 * @throws Exception
	 */
	public static byte[] getByteArrayFromInputStream(InputStream is)
			throws Exception {
		if (is == null || is.available() < 1) {
			return new byte[0];
		}
		byte[] buff = new byte[8192];
		byte[] result = new byte[is.available()];
		int nch;
		BufferedInputStream in = new BufferedInputStream(is);
		int pos = 0;
		while ((nch = in.read(buff, 0, buff.length)) != -1) {
			System.arraycopy(buff, 0, result, pos, nch);
			pos += nch;
		}
		in.close();
		return result;
	}

	/**
	 * 生成一个填充字符串
	 * 
	 * @param value
	 *            value
	 * @param length
	 *            length
	 * @return 填充字符串
	 */
	public static String newString(String value, int length) {
		StringBuffer buffer = new StringBuffer();
		if (value == null) {
			return null;
		}
		for (int i = 0; i < length; i++) {
			buffer.append(value);
		}

		return buffer.toString();
	}

	/**
	 * 抽样
	 * 
	 * @param source
	 *            source Collection
	 * @param sampleCount
	 *            sampleCount
	 * @return result Collection
	 */
	public static Collection sample(Object[] source, int sampleCount) {
		Collection result = new ArrayList();
		if (source == null) {
			return null;
		}
		int count = source.length;
		int[] keys = sample(count, sampleCount);
		for (int i = 0; i < keys.length; i++) {
			result.add(source[i]);
		}
		return result;
	}

	/**
	 * 取得随机数组
	 * 
	 * @param maxNo
	 *            maxNo
	 * @param sampleCount
	 *            sampleCount
	 * @return int[]
	 */
	public static int[] sample(int maxNo, int sampleCount) {
		Hashtable hash = new Hashtable();

		if (sampleCount > maxNo) {
			sampleCount = maxNo;
		}

		int[] keys = new int[sampleCount];
		int sampleIndex = 0;
		int index = 0;

		for (int i = 0; i < sampleCount; i++) {
			while (true) {
				sampleIndex = (int) Math.round(Math.random() * (maxNo - 1));
				if (!hash.containsKey("" + sampleIndex)) {
					hash.put("" + sampleIndex, "" + sampleIndex);
					break;
				}
			}
			keys[index++] = sampleIndex;
		}
		Arrays.sort(keys);
		return keys;
	}

	/**
	 * 字符串 转换成HTML格式
	 * 
	 * @param strInValue
	 *            传入字符串
	 * @return String 传入字符串
	 */
	public static String toHTMLFormat(String strInValue) {
		String strOutValue = "";
		char c;
		for (int i = 0; i < strInValue.length(); i++) {
			c = strInValue.charAt(i);
			switch (c) {
			case '<':
				strOutValue += "&lt;";
				break;
			case '>':
				strOutValue += "&gt;";
				break;
			case '\n':
				strOutValue += "<br>";
				break;
			case '\r':
				break;
			case ' ':
				strOutValue += "&nbsp;";
				break;
			default:
				strOutValue += c;
				break;
			}
		}
		return strOutValue;
	}

	/**
	 * 将指定的字符串按给定的最大长度进行分割，返回分割后的字符串数组
	 * 
	 * @param strMain
	 *            被拆分的串
	 * @param intMaxLength
	 *            每一行结果串的长度最大值
	 * @return Object 分割字符串后的字符串数组
	 */
	public static Object split(String strMain, int intMaxLength) {
		// 定义变量
		Vector vector = new Vector(); // 存放截后的字符串
		String strText = ""; // 临时存放字符串
		byte[] arrByte = null; // 被拆分的字符串生成的Byte数组
		int intStartIndex = 0; // 游标起始位置
		int intEndIndex = 0; // 游标终止位置
		int index = 0;
		int count = 0;
		String[] arrReturn = null; // 返回

		// 特殊值处理（长度<=1、空、空字符串）
		if (intMaxLength <= 1) {
			System.err.println("error: intMaxLength <= 1");
			return null;
		}
		if (strMain == null) {
			return new String[0]; // 空数组
		}
		if (strMain.trim().equals("")) {
			return new String[] { "" }; // 空字符串
		}

		// 正常处理
		arrByte = strMain.getBytes();
		intEndIndex = 0; // 设置最初值

		while (true) {
			// 初步设置游标位置
			intStartIndex = intEndIndex;
			intEndIndex = intStartIndex + intMaxLength;

			// 起始位置已经超过数组长度
			if (intStartIndex >= arrByte.length) {
				break;
			}

			// 终止位置已经超过数组长度
			if (intEndIndex > arrByte.length) {
				intEndIndex = arrByte.length;
				strText = new String(arrByte, intStartIndex, intEndIndex
						- intStartIndex);
				vector.add(strText);
				break;
			}

			// 检查末尾的半个汉字问题
			count = 0;
			for (index = intStartIndex; index < intEndIndex; index++) {
				if (arrByte[index] < 0) {
					count++;
				}
			}
			// 出现半个汉字
			if (count % 2 != 0) {
				intEndIndex--;

				// 构造字串
			}
			strText = new String(arrByte, intStartIndex, intEndIndex
					- intStartIndex);
			vector.add(strText);
		} // end while

		// 转成字符串数组
		arrReturn = new String[vector.size()];
		for (index = 0; index < vector.size(); index++) {
			arrReturn[index] = (String) vector.get(index);
		}

		// 返回
		return arrReturn;
	}

	/**
	 * Main
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {

	}

	public static String right(String s, int length) {
		// 条件
		if (s == null) {
			return s;
		}

		if (length <= 0) {
			return "";
		}

		// 长度过小
		int count = s.length();

		if (count <= length) {
			return s;
		}

		return s.substring(s.length() - length);
	}
	
	/**
	 *  判断一个字符串是否为空
	 * @param str
	 * @return
	 * @author wmz
	 * @version 2008-12-18
	 */
	public static boolean isEmptyString(String str) {
		return (null == str || str.trim().length() == 0);
	}
}