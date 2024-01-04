package sqlite.domain.schema;

public record Table(
	String name,
	long rootPage
) {}