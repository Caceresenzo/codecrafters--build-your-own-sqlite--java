package sqlite.domain.schema;

import java.util.List;

public record Schema(
	List<Table> tables
) {}