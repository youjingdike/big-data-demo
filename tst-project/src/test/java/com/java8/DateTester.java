package com.java8;

import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateTester {
	
	@Test
	public void testDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		System.out.println(LocalDate.now());
		
		System.out.println(LocalTime.now());
		
		System.out.println(LocalDateTime.now());
		LocalDateTime parse = LocalDateTime.parse("2017-12-31 12:33:33", formatter);
		System.out.println(parse);
		System.out.println(LocalDateTime.now().format(formatter));
		
		System.out.println(ZonedDateTime.now());
		System.out.println(LocalDateTime.now().format(formatter));
		System.out.println(ZonedDateTime.now().format(formatter));

	}
	
	@Test
	public void testClock() {
		// Get the system clock as UTC offset 
		final Clock clock = Clock.systemUTC();
		System.out.println( clock.instant() );
		System.out.println( clock.millis() );//可以替换System.currentTimeMillis()
		
		System.out.println( System.currentTimeMillis() );
		System.out.println( TimeZone.getDefault() );
		
	}
	
	@Test
	public void testLocalDateAndTime() {
		// Get the local date and local time
		final LocalDate date = LocalDate.now();
		
//		final Clock clock = Clock.systemUTC();
		final Clock clock = Clock.system(ZoneId.of("Asia/Shanghai"));
		final LocalDate dateFromClock = LocalDate.now( clock );
		         
		System.out.println( date );
		System.out.println( dateFromClock );
		         
		// Get the local date and local time
		final LocalTime time = LocalTime.now();
		final LocalTime timeFromClock = LocalTime.now( clock );
		         
		System.out.println( time );
		System.out.println( timeFromClock );
		
		// Get the local date/time
		final LocalDateTime datetime = LocalDateTime.now();
		final LocalDateTime datetimeFromClock = LocalDateTime.now( clock );
		         
		System.out.println( datetime );
		System.out.println( datetimeFromClock );
		
		// Get the zoned date/time
		final ZonedDateTime zonedDatetime = ZonedDateTime.now();
		final ZonedDateTime zonedDatetimeFromClock = ZonedDateTime.now( clock );
		final ZonedDateTime zonedDatetimeFromZone = ZonedDateTime.now( ZoneId.of( "America/Los_Angeles" ) );
		         
		System.out.println( zonedDatetime );
		System.out.println( zonedDatetimeFromClock );
		System.out.println( zonedDatetimeFromZone );
		
		// Get duration between two dates
		final LocalDateTime from = LocalDateTime.of( 2014, Month.APRIL, 16, 0, 0, 0 );
		final LocalDateTime to = LocalDateTime.of( 2015, Month.APRIL, 16, 23, 59, 59 );
		final Duration duration = Duration.between( from, to );
		System.out.println( "Duration in days: " + duration.toDays() );
		System.out.println( "Duration in hours: " + duration.toHours() );
	}
	
}

