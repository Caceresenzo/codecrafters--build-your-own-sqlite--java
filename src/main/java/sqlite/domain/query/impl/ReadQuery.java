package sqlite.domain.query.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import sqlite.SQLiteParser;
import sqlite.domain.Cell;
import sqlite.domain.Database;
import sqlite.domain.TableRow;
import sqlite.domain.TextEncoding;
import sqlite.domain.query.LeafIndexIterator;
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
	public Iterator<TableRow> execute(Database database) {
		final var textEncoding = database.header().textEncoding();
		final var schema = database.schema();
		final var table = schema.table(tableName);

		final var columnIndexes = getColumnIndexes(table);

		final var predicate = namePredicate != null
			? namePredicate.convert(table.columnNames())
			: null;

		final var index = namePredicate != null
			? schema.findIndex(tableName, namePredicate.columnName()).orElse(null)
			: null;

		if (index == null) {
			return new RowIterator(
				new LeafTableIterator(database::page, table.rootPage()),
				database.header().textEncoding(),
				predicate,
				columnIndexes
			);
		}

		final var iterator = new LeafIndexIterator(database::page, index.rootPage(), textEncoding, predicate.value());
		return new RowIteratorViaIndex(database, table, iterator, textEncoding, columnIndexes);
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
	public class RowIterator implements Iterator<TableRow> {

		private final LeafTableIterator delegate;
		private final TextEncoding textEncoding;
		private final ColumnIndexPredicate predicate;
		private final List<Integer> columnIndexes;
		private TableRow next;

		@Override
		public boolean hasNext() {
			next = peek();
			return next != null;
		}

		@Override
		public TableRow next() {
			return next;
		}

		private TableRow peek() {
			while (delegate.hasNext()) {
				final var leaf = delegate.next();
				var row = SQLiteParser.parseTableRow(leaf, textEncoding);

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

	@RequiredArgsConstructor
	public class RowIteratorViaIndex implements Iterator<TableRow> {

		private final Database database;
		private final Table table;
		private final LeafIndexIterator delegate;
		private final TextEncoding textEncoding;
		private final List<Integer> columnIndexes;
		private TableRow next;

		@Override
		public boolean hasNext() {
			next = peek();
			return next != null;
		}

		@Override
		public TableRow next() {
			return next;
		}

		private TableRow peek() {
			while (delegate.hasNext()) {
				final var rowId = delegate.next();

				var row = findByRowId(rowId, table.rootPage());
				if (row == null) {
					throw new IllegalStateException("no row found with id: " + rowId);
				}

				if (columnIndexes != null) {
					row = row.reIndex(columnIndexes);
				}

				return row;
			}

			return null;
		}

		private TableRow findByRowId(long rowId, long pageNumber) {
			final var page = database.page(pageNumber);

			for (final var cell : page.cells()) {
				if (cell instanceof Cell.InteriorTable interiorTable) {
					if (interiorTable.rowId() < rowId) {
						continue;
					}

					final var row = findByRowId(rowId, interiorTable.leftChild());
					if (row != null) {
						return row;
					}
				} else if (cell instanceof Cell.LeafTable leafTable) {
					if (leafTable.rowId() != rowId) {
						continue;
					}

					return SQLiteParser.parseTableRow(leafTable, textEncoding);
				}
			}

			final var header = page.header();
			if (header.type().hasRightMost()) {
				return findByRowId(rowId, header.rightMost());
			}

			return null;
		}

	}

}