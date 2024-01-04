package sqlite.domain.query;

import java.util.Iterator;
import java.util.List;

import sqlite.domain.Database;
import sqlite.domain.Row;

public record CountQuery(
	String tableName
) implements Query {

	@Override
	public Iterator<Row> execute(Database database) {
		final var table = database.schema().table(tableName);

		var count = 0l;

		final var iterator = new LeafTableIterator(database, table.rootPage());
		while (iterator.hasNext()) {
			iterator.next();
			++count;
		}

		final var row = new Row(-1, List.of(count));
		return List.of(row).iterator();
	}

}