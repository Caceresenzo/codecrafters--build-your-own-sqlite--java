package sqlite.domain.schema;

import java.util.List;
import java.util.Optional;

public record Schema(
	List<Table> tables,
	List<Index> indexes
) {

	public Table table(String name) {
		for (final var table : tables) {
			if (table.name().equalsIgnoreCase(name)) {
				return table;
			}
		}

		throw new IllegalArgumentException("table not found: " + name);
	}

	public Optional<Index> findIndex(String tableName, String columnName) {
		for (final var index : indexes) {
			if (index.match(tableName, columnName)) {
				return Optional.of(index);
			}
		}

		return Optional.empty();
	}

}