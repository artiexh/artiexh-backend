package com.artiexh.api.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	private DateTimeUtils() {
	}

	public static LocalDateTime stringToInstant(String dateTime, String pattern) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));

	}
}
