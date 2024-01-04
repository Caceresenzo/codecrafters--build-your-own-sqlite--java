package sqlite.domain.query.predicate;

import java.util.function.Predicate;

import sqlite.domain.Row;

public record ColumnIndexPredicate(
	int columnIndex,
	String value
) implements Predicate<Row> {

	@Override
	public boolean test(Row row) {
		return value.equals(row.getString(columnIndex));
	}

}