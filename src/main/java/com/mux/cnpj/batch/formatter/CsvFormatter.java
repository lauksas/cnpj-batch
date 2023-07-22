package com.mux.cnpj.batch.formatter;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CsvFormatter {

	public static BigDecimal toBigDecimal(String numberString) {
		try {
			if (numberString == null)
				return null;

			numberString = numberString.trim();

			if ("".equals(numberString))
				return null;

			return new BigDecimal(numberString.replaceAll(",", "."));
		} catch (Exception e) {
			log.warn("Problem parsing {} to number", numberString);
			return null;
		}
	}

	public static Integer toInteger(String numberString) {
		try {
			if (numberString == null)
				return null;

			numberString = numberString.trim();

			if ("".equals(numberString))
				return null;

			return Integer.parseInt(numberString.replaceAll(",", "."));
		} catch (Exception e) {
			log.warn("Problem parsing {} to number", numberString);
			return null;
		}
	}

	public static String intAsText(String numberString) {
		try {
			if (numberString == null)
				return null;

			numberString = numberString.trim();

			if ("".equals(numberString))
				return null;

			return String.valueOf(Integer.parseInt(numberString.replaceAll(",", ".")));
		} catch (Exception e) {
			return numberString;
		}
	}

	public static String nullIfEmpty(String s) {
		String result = null;
		if (s != null) {
			result = s.trim().isBlank() ? null : s.trim();
		}
		return result;
	}

	public static Integer telToInt(String tel) {
		String result = nullIfEmpty(tel);
		if (result == null)
			return null;

		return toInteger(result.replaceAll("-", "").replaceAll(" ", ""));
	}
}
