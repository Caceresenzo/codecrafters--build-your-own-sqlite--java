package sqlite.domain;

import java.nio.ByteBuffer;

import sqlite.SQLiteParser;
import sqlite.domain.schema.Schema;

public record Database(
	DatabaseHeader header,
	Schema schema,
	ByteBuffer buffer
) {

	public Page page(long number) {
		return SQLiteParser.parsePage(buffer, header.pageSize(), Math.toIntExact(number));
	}

}