package java8.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
	/**
	 * 格式：yyyyMMddHHmmssSSS
	 */
	public static final DateTimeFormatter YMDHMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	/**
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	/**
	 * 格式：yyyy-MM-dd
	 */
	public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
	 * 格式：yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final DateTimeFormatter DATE_TIME_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * 获取相应格式的日期字符串
	 * @param formatter
	 */
	public static String getNowDateStr(DateTimeFormatter formatter) {
		return LocalDateTime.now().format(formatter);
	}
	
	/**
	 * 获取相应格式的日期字符串
	 * @param formatter
	 */
	public static String getDateStr(LocalDateTime now,DateTimeFormatter formatter) {
		return now.format(formatter);
	}
	
	/**
	 * 计算传入的时间字符串，与当前时间的天数差
	 * @param dateStr
	 * @return
	 */
	public static int getBetweenDaysToNow(String dateStr,LocalDate now) {
		return (int) LocalDate.parse(dateStr, DATE).until(now, ChronoUnit.DAYS);
	}
	
	/**
	 * 计算传入的时间字符串之间的天数差
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static int getBetweenDaysNum(String startStr,String endStr) {
		return (int) LocalDate.parse(startStr, DATE).until(LocalDate.parse(endStr,DATE), ChronoUnit.DAYS);
	}
	
	/**
	 * LocalDate转换成Date
	 * @param localDate
	 * @return
	 *//*
	public static Date LocalDateToUdate(LocalDate localDate) {
	    ZoneId zone = ZoneId.systemDefault();
	    Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
	    return Date.from(instant);
	}
	
	*//**
	 * 获取localDate的毫秒数
	 * @param localDate
	 * @return
	 *//*
	public static long LocalDateToLong(LocalDate localDate) {
	    return LocalDateToUdate(localDate).getTime();
	}*/
	
	/**
	 * 获取localDate的num天之后的localDate
	 * @param localDate
	 * @return
	 */
	public static LocalDate getLocalDateAfterNum(String localDate,long num) {
	    return LocalDate.parse(localDate, DATE).plusDays(num);
	}
	
	public static void main(String[] args) {
		System.out.println(LocalDate.now());
		System.out.println(LocalDateTime.parse("2018-05-21 13:13:12", DATE_TIME).toLocalDate());
		Period between = Period.between(LocalDate.now(), LocalDateTime.parse("2018-04-28 13:13:12", DATE_TIME).toLocalDate());
		System.out.println(between.getDays());
		System.out.println(LocalDate.parse("2018-03-28", DATE).until(LocalDate.now(), ChronoUnit.DAYS));
		System.out.println(getLocalDateAfterNum("2018-03-28",1));
		System.out.println(getBetweenDaysNum("2018-03-28","2018-03-30"));
	}
	
}
