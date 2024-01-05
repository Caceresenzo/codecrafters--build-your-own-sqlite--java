package sqlite.domain.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import sqlite.SQLiteParser;
import sqlite.domain.Cell;
import sqlite.domain.Page;
import sqlite.domain.TextEncoding;
import sqlite.domain.query.predicate.ColumnIndexPredicate;

public class LeafIndexIterator implements Iterator<Long>, Iterable<Long> {

	private final Function<Long, Page> pageReader;
	private final List<Long> pageNumbers;
	private final List<Long> rowIds;
	private final TextEncoding textEncoding;
	private final ColumnIndexPredicate predicate;

	public LeafIndexIterator(Function<Long, Page> pageReader, long firstPageNumber, TextEncoding textEncoding, String valueToMatch) {
		this.pageReader = pageReader;
		this.pageNumbers = new LinkedList<>();
		this.rowIds = new LinkedList<>();
		this.textEncoding = textEncoding;
		this.predicate = new ColumnIndexPredicate(0, valueToMatch);

		pageNumbers.addFirst(firstPageNumber);
	}

	@Override
	public boolean hasNext() {
		return peek() != null;
	}

	@Override
	public Long next() {
		return rowIds.removeFirst();
	}

	public Long peek() {
		while (rowIds.isEmpty() && !pageNumbers.isEmpty()) {
			final var number = pageNumbers.removeFirst();
			final var page = pageReader.apply(number);

			var testPassed = false;
			for (final var cell : page.cells()) {
				if (cell instanceof Cell.InteriorIndex interiorIndex) {
					pageNumbers.addLast(interiorIndex.leftChild());
				} else if (cell instanceof Cell.LeafIndex leafIndex) {
					final var row = SQLiteParser.parseIndexRow(leafIndex, textEncoding);

					if (!testPassed) {
						if (!predicate.test(row)) {
							break;
						}

						testPassed = true;
					}

					rowIds.add(row.id());
				} else {
					throw new IllegalStateException("unexpected cell: " + cell);
				}
			}

			final var header = page.header();
			if (header.type().hasRightMost()) {
				pageNumbers.addLast(header.rightMost());
			}
		}

		if (!rowIds.isEmpty()) {
			return rowIds.getFirst();
		}

		return null;
	}

	@Override
	public Iterator<Long> iterator() {
		return this;
	}

}