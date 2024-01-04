package sqlite.domain.query.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import sqlite.SQLiteParser;
import sqlite.domain.Database;
import sqlite.domain.Row;
import sqlite.domain.TextEncoding;
import sqlite.domain.query.LeafTableIterator;
import sqlite.domain.query.Query;
import sqlite.domain.query.predicate.ColumnIndexPredicate;
import sqlite.domain.query.predicate.ColumnNamePredicate;
import sqlite.domain.schema.Table;

public record ReadQuery(
	String tableName,
	List<String> columnNames,
	ColumnNamePredicate namePredicate
) implements Query {

	@Override
	public Iterator<Row> execute(Database database) {
		final var table = database.schema().table(tableName);
		final var columnIndexes = getColumnIndexes(table);

		final var predicate = namePredicate != null
			? namePredicate.convert(table.columnNames())
			: null;

		return new RowIterator(
			new LeafTableIterator(database::page, table.rootPage()),
			database.header().textEncoding(),
			predicate,
			columnIndexes
		);
	}

	public List<Integer> getColumnIndexes(Table table) {
		if (columnNames.size() == 1 && "*".equals(columnNames.getFirst())) {
			return null /* all */;
		}

		final var columnIndexes = new ArrayList<Integer>(columnNames.size());

		final var tableColumnNames = table.columnNames();
		for (final var columnName : columnNames) {
			final var index = tableColumnNames.indexOf(columnName);

			if (index == -1) {
				throw new IllegalArgumentException("column not found: " + columnName);
			}

			columnIndexes.add(index);
		}

		if (tableColumnNames.equals(columnIndexes)) {
			return null /* all */;
		}

		return columnIndexes;
	}

	@RequiredArgsConstructor
	public class RowIterator implements Iterator<Row> {

		private final LeafTableIterator delegate;
		private final TextEncoding textEncoding;
		private final ColumnIndexPredicate predicate;
		private final List<Integer> columnIndexes;
		private Row next;

		@Override
		public boolean hasNext() {
			next = peek();
			return next != null;
		}

		@Override
		public Row next() {
			return next;
		}

		private Row peek() {
			while (delegate.hasNext()) {
				final var leaf = delegate.next();
				var row = SQLiteParser.parseRow(leaf, textEncoding);

				if (predicate != null && !predicate.test(row)) {
					continue;
				}

				if (columnIndexes != null) {
					row = row.reIndex(columnIndexes);
				}

				return row;
			}

			return null;
		}

	}

}