package com.artiexh.api.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	private DateTimeUtils() {
	}

	public static Instant stringToInstant(String dateTime, String pattern, ZoneId zoneId) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern))
			.atZone(zoneId)
			.toInstant();

	}
}
