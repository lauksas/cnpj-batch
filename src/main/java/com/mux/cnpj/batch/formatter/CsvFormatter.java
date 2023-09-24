package com.mux.cnpj.batch.formatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mux.cnpj.batch.data.entity.Cnae;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CsvFormatter {

	public static BigDecimal toBigDecimal(String numberString) {
		numberString = nullIfEmpty(numberString);

		if (numberString == null)
			return null;

		try {
			return new BigDecimal(numberString.replaceAll(",", "."));
		} catch (Exception e) {
			log.warn("Problem parsing {} to BigDecimal", numberString);
			return null;
		}
	}

	public static BigInteger toBigInteger(String numberString) {
		numberString = nullIfEmpty(numberString);

		if (numberString == null)
			return null;

		try {
			return new BigInteger(numberString);
		} catch (Exception e) {
			log.warn("Problem parsing {} to BigDecimal", numberString);
			return null;
		}
	}

	public static Integer toInteger(String numberString) {
		numberString = nullIfEmpty(numberString);
		if (numberString == null)
			return null;

		try {
			return Integer.parseInt(numberString.replaceAll(",", "."));
		} catch (Exception e) {
			log.warn("Problem parsing {} to Integer", numberString);
			return null;
		}

	}

	public static String intAsText(String numberString) {
		try {
			numberString = nullIfEmpty(numberString);
			if (numberString == null)
				return null;

			return String.valueOf(Integer.parseInt(numberString.replaceAll(",", ".")));
		} catch (Exception e) {
			return numberString;
		}
	}

	private static Pattern nullPattern = Pattern.compile("\0");

	public static String nullIfEmpty(String s) {
		String result = null;
		if (s != null) {
			String trimmed = nullPattern.matcher(s.trim()).replaceAll("");
			result = trimmed.isBlank() ? null : trimmed;
		}
		return result;
	}

	public static Integer telToInt(String tel) {
		String result = nullIfEmpty(tel);
		if (result == null)
			return null;

		return toInteger(result.replaceAll("-", "").replaceAll(" ", ""));
	}

	public static Boolean fromString(String val) {
		val = nullIfEmpty(val);
		Boolean result = null;
		val = val != null ? val.toUpperCase() : null;
		if ("S".equals(val)) {
			result = Boolean.TRUE;
		} else if ("N".equals(val)) {
			result = Boolean.FALSE;
		}
		return result;
	}

	public static Set<Cnae> fromCsvString(String csv) {
		Set<Cnae> result = null;
		csv = nullIfEmpty(csv);

		if (csv == null)
			return null;

		String[] cnaesText = csv.split(",");
		result = Arrays.stream(cnaesText)
				.map(cenaeId -> Cnae.builder().id(toInteger(cenaeId)).build())
				.collect(Collectors.toSet());

		return result;
	}
}
