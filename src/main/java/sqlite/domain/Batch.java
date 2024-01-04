package sqlite.domain;

import java.util.List;

public record Batch(
	long id,
	List<Row> rows
) {}