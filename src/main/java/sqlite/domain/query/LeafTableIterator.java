package sqlite.domain.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sqlite.domain.Cell;
import sqlite.domain.Database;

public class LeafTableIterator implements Iterator<Cell.LeafTable>, Iterable<Cell.LeafTable> {

	private final Database database;
	private final List<Long> pageNumbers;
	private final List<Cell.LeafTable> leafTables;

	public LeafTableIterator(Database database, long firstPageNumber) {
		this.database = database;
		this.pageNumbers = new LinkedList<>();
		this.leafTables = new LinkedList<>();

		pageNumbers.addFirst(firstPageNumber);
	}

	@Override
	public boolean hasNext() {
		return peek() != null;
	}

	@Override
	public Cell.LeafTable next() {
		return leafTables.removeFirst();
	}

	public Cell.LeafTable peek() {
		while (leafTables.isEmpty() && !pageNumbers.isEmpty()) {
			final var number = pageNumbers.removeFirst();
			final var page = database.page(number);

			for (final var cell : page.cells()) {
				if (cell instanceof Cell.InteriorTable interiorTable) {
					pageNumbers.addLast(interiorTable.leftChild());
				} else if (cell instanceof Cell.LeafTable leafTable) {
					leafTables.addLast(leafTable);
				} else {
					throw new IllegalStateException("unexpected cell: " + cell);
				}
			}

			final var header = page.header();
			if (header.type().hasRightMost()) {
				pageNumbers.addLast(header.rightMost());
			}
		}

		if (!leafTables.isEmpty()) {
			return leafTables.getFirst();
		}

		return null;
	}

	@Override
	public Iterator<Cell.LeafTable> iterator() {
		return this;
	}

}