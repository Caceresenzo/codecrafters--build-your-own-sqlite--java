package sqlite.domain.query.predicate;

import java.util.List;

public record ColumnNamePredicate(
	String columnName,
	String value
) {
	
	public ColumnIndexPredicate convert(List<String> columnNames) {
		final var index = columnNames.indexOf(columnName);
		if (index == -1) {
			throw new IllegalArgumentException("unknown column: " + columnName);
		}
		
		return new ColumnIndexPredicate(index, value);
	}
	
}