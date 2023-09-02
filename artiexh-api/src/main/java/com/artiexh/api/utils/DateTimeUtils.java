package com.artiexh.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
	private DateTimeUtils() {}

	public static LocalDateTime stringToInstant(String dateTime, String pattern) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));

	}
}
