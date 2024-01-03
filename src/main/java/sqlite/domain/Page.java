package sqlite.domain;

import java.util.List;

public record Page(
	PageHeader header,
	List<Cell> cells
) {}