package org.ipph.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DATETIME_NOSECOND_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat TIME_NOSECOND_FORMAT = new SimpleDateFormat("HH:mm");
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final DateFormat DATE_ZH_FORMAT = new SimpleDateFormat("yyyy年 MM月 dd日");

	public static Date parse(String dateString) throws ParseException {
		//trim方法去掉前后的空格,如果格式为yyyy-MM-dd HH:mm:ss.SSS，即包含空格，又包含。
		dateString=dateString.trim();
		if ((dateString.indexOf(" ") > 0)&& (dateString.indexOf(".") > 0)) {
			return new Timestamp(TIMESTAMP_FORMAT.parse(dateString).getTime());
		}
		else if (dateString.indexOf(" ") > 0) {//yyyy-MM-dd HH:mm:ss
			if (dateString.indexOf(":") != dateString.lastIndexOf(":")) {
				return new Timestamp(DATETIME_FORMAT.parse(dateString).getTime());
			}
			//yyyy-MM-dd HH:mm
			return new Timestamp(DATETIME_NOSECOND_FORMAT.parse(dateString).getTime());
		}
		
		else if (dateString.indexOf(":") > 0) {//HH:mm:ss
			if (dateString.indexOf(":") != dateString.lastIndexOf(":")) {
				return new Time(TIME_FORMAT.parse(dateString).getTime());
			}
			//HH:mm
			return new Time(TIME_NOSECOND_FORMAT.parse(dateString).getTime());
		}
		//yyyy-MM-dd，默认使用
		return new Date(DATE_FORMAT.parse(dateString).getTime());
	}
	/**
	 * 将日期对象格式化成字符串显示
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if (date instanceof Timestamp)
			return TIMESTAMP_FORMAT.format(date);
		if (date instanceof Time)
			return TIME_FORMAT.format(date);
		if (date instanceof Date)
			return DATE_FORMAT.format(date);

		return DATETIME_FORMAT.format(date);
	}
	/**
	 * 使用指定格式的日期样式。将字符串parse成Date
	 * @param dateString
	 * @param style
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateString, String style)throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(style);
		return dateFormat.parse(dateString);
	}
	/**
	 * 使用指定格式的日期样式。将Date对象fromat成字符串
	 * @param date
	 * @param style
	 * @return
	 */
	public static String format(Date date, String style) {
		DateFormat dateFormat = new SimpleDateFormat(style);
		return dateFormat.format(date);
	}

	public static Date parseDate(String dateString) throws ParseException {
		return DATE_FORMAT.parse(dateString);
	}

	public static String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}

	public static Date parseDateTime(String dateString) throws ParseException {
		return DATETIME_FORMAT.parse(dateString);
	}

	public static String formaDatetTime(Date date) {
		return DATETIME_FORMAT.format(date);
	}

	public static String formatTimeNoSecond(Date date) {
		return DATETIME_NOSECOND_FORMAT.format(date);
	}

	public static Date parseTimeNoSecond(String dateString)
			throws ParseException {
		return DATETIME_NOSECOND_FORMAT.parse(dateString);
	}

	/**
	 * 获取当前日期的时间戳，可以指定样式
	 * @param style
	 * @return
	 */
	public static String getNowByString(String style) {
		if ((null == style) || ("".equals(style)))
			style = "yyyy-MM-dd HH:mm:ss";

		return format(new Date(), style);
	}
	
	/**
	 * 使用中文形式的日期格式化Date镀锡
	 * @param date
	 * @return
	 */
	public static String formatDateZh(Date date) {
		return DATE_ZH_FORMAT.format(date);
	}
}
