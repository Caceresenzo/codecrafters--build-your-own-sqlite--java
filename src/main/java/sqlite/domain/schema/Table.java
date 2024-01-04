package sqlite.domain.schema;

import java.util.List;

public record Table(
	String name,
	long rootPage,
	String sql,
	List<String> columnNames
) {}