package sqlite.domain.query.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sqlite.domain.Database;
import sqlite.domain.Row;
import sqlite.domain.query.LeafTableIterator;
import sqlite.domain.query.Query;

public record CountQuery(
	String tableName
) implements Query {

	@Override
	public Iterator<Row> execute(Database database) {
		final var table = database.schema().table(tableName);

		var count = 0l;

		final var iterator = new LeafTableIterator(database::page, table.rootPage());
		while (iterator.hasNext()) {
			iterator.next();
			++count;
		}

		final var row = new Row(-1, List.of(count), Collections.emptyList());
		return List.of(row).iterator();
	}

}