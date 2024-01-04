package sqlite.domain.schema;

import java.util.List;

public record Schema(
	List<Table> tables
) {

	public Table table(String name) {
		for (final var table : tables) {
			if (table.name().equalsIgnoreCase(name)) {
				return table;
			}
		}

		throw new IllegalArgumentException("table not found: " + name);
	}

}