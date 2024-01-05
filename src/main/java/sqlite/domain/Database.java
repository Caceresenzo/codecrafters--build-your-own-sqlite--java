package sqlite.domain;

import java.nio.channels.FileChannel;

import sqlite.SQLiteParser;
import sqlite.domain.schema.Schema;

public record Database(
	DatabaseHeader header,
	Schema schema,
	FileChannel channel
) {

	public Page page(long number) {
		return SQLiteParser.parsePage(channel, header.pageSize(), Math.toIntExact(number));
	}

}