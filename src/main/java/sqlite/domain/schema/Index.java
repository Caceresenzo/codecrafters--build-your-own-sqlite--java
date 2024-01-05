package sqlite.domain.schema;

public record Index(
	String name,
	String tableName,
	long rootPage,
	String sql,
	String columnName
) {

	public boolean match(String tableName, String columnName) {
		return this.tableName.equalsIgnoreCase(tableName) && this.columnName.equalsIgnoreCase(columnName);
	}

}