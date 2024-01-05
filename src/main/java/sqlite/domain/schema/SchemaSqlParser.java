package sqlite.domain.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SchemaSqlParser {

	public static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("CREATE TABLE .+?\\s*\\((.+?)\\)", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	public static final Pattern COLUMN_PATTERN = Pattern.compile("(?:(\\w+)|\"(.*?)\").*?(?:,|$)", Pattern.MULTILINE | Pattern.DOTALL);
	public static final Pattern CREATE_INDEX_PATTERN = Pattern.compile("^CREATE INDEX (\\w+)\\s+ON (\\w+) \\((\\w+)\\)$", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	public static List<String> parseColumnNames(String sql) {
		final var columnNames = new ArrayList<String>();

		var matcher = CREATE_TABLE_PATTERN.matcher(sql);
		if (!matcher.find()) {
			throw new IllegalStateException("`CREATE TABLE` not matched: " + sql);
		}

		final var columnsSql = matcher.group(1);

		matcher = COLUMN_PATTERN.matcher(columnsSql);
		while (matcher.find()) {
			final var first = matcher.group(1);
			final var second = matcher.group(2);

			if (first == null) {
				columnNames.add(second);
			} else {
				columnNames.add(first);
			}
		}

		//		System.out.println(columnsSql);
		//		System.out.println(columnNames);
		return columnNames;
	}

	public static String parseIndexColumnName(String sql) {
		final var matcher = CREATE_INDEX_PATTERN.matcher(sql);
		if (!matcher.find()) {
			throw new IllegalStateException("`CREATE INDEX` not matched: " + sql);
		}

		return matcher.group(3);
	}

}