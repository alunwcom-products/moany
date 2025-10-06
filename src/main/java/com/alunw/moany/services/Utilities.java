package com.alunw.moany.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

//import javax.annotation.PostConstruct;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for common helper methods
 */
public class Utilities {

	private static Logger LOG = LoggerFactory.getLogger(Utilities.class);

	public static final DateTimeFormatter SYSTEM_DATE_PARAMETER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter[] STATEMENT_DATE_PARAMETER_FORMATS = {
		DateTimeFormatter.ofPattern("dd/MM/yyyy"),
		DateTimeFormatter.ofPattern("dd MMM yyyy"),
		DateTimeFormatter.ofPattern("dd-MMM-yy")
	};

	@PostConstruct
	public void init() {
		//DATE_PARAMETER_FORMAT.setLenient(false);
	}

	public static LocalDate parseStatementDate(String date) {

		LocalDate result = null;
		for (DateTimeFormatter dateFormat : STATEMENT_DATE_PARAMETER_FORMATS) {
			try {
				result = LocalDate.parse(date, dateFormat);
				// break if success
				break;
			} catch(DateTimeParseException e) {
				// ignore
			}
		}

		return result;
	}

	public static Optional<LocalDate> parseDate(String date, String... formats) {

		Optional<LocalDate> result = Optional.empty();

		for (String dateFormat : formats) {
			try {
				result = Optional.ofNullable(LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat)));
				if (result.isPresent()) {
					break;
				}
			} catch(DateTimeParseException e) {
				// ignore
			}
		}

		return result;
	}

	public static LocalDate getDateFromString(String dateParameter) {
		return getDateFromString(dateParameter, SYSTEM_DATE_PARAMETER_FORMAT);
	}

	public static LocalDate getDateFromString(String dateParameter, DateTimeFormatter format) {

		LocalDate dateValue = null;

		if (dateParameter == null || dateParameter.isEmpty()) {
			return null;
		}

		try {
			dateValue = LocalDate.parse(dateParameter, format);
		} catch (DateTimeParseException e) {
			LOG.warn("Unable to parse date parameter: " + dateParameter);
		}

		return dateValue;
	}

	public static LocalDate getEarliestDate() {
		return getDateFromString("1970-01-01");
	}

	public static LocalDate getLatestDate() {
		return getDateFromString("9999-12-31");
	}

	public static String getFormattedDate(LocalDate date) {

		String result = "";
		if (date != null) {
			result = SYSTEM_DATE_PARAMETER_FORMAT.format(date);
		}

		return result;
	}
}
