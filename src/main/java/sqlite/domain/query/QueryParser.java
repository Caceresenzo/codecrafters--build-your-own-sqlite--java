package sqlite.domain.query;

import java.util.Arrays;
import java.util.regex.Pattern;

import sqlite.domain.query.impl.CountQuery;
import sqlite.domain.query.impl.ReadQuery;

public class QueryParser {

	public static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT (.+?) FROM (.+?)(?: WHERE (.+?))?$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

	public static Query parse(String querySql) {
		final var matcher = SELECT_PATTERN.matcher(querySql);
		if (!matcher.find()) {
			throw new IllegalStateException("invalid sql: " + querySql);
		}

		final var joinedColumns = matcher.group(1);
		final var tableName = matcher.group(2);
		//		final var joinedConditions = matcher.group(3);

		if ("COUNT(*)".equalsIgnoreCase(joinedColumns)) {
			return new CountQuery(tableName);
		}

		final var columns = Arrays.asList(joinedColumns.split(",\\s*"));
		return new ReadQuery(tableName, columns);
	}

}