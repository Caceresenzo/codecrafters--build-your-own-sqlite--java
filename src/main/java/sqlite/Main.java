package sqlite;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import sqlite.domain.Database;
import sqlite.domain.schema.Table;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Missing <database path> and <command>");
			return;
		}

		final var path = Path.of(args[0]);
		final var database = SQLiteParser.parse(path);

		final var command = args[1];
		switch (command) {
			case ".dbinfo" -> dotDbinfo(database);
			case ".tables" -> dotTables(database);
			default -> System.out.println("Missing or invalid command passed: " + command);
		}
	}

	public static void dotDbinfo(Database database) {
		final var header = database.header();
		print("database page size", header.pageSize());
		print("write format", header.writeVersion().value());
		print("read format", header.readVersion().value());
		print("reserved bytes", header.reservedBytes());
		print("file change counter", header.fileChangeCounter());
		print("database page count", header.pageCount());
		print("freelist page count", header.freelistPageCount());
		print("schema cookie", header.schemaCookie());
		print("schema format", header.schemaFormat());
		print("default cache size", header.defaultPageCacheSize());
		print("autovacuum top root", header.autovacuumTopRoot());
		print("incremental vacuum", header.incrementalVacuum());
		print("text encoding", header.textEncoding().format());
		print("user version", header.userVersion());
		print("application id", header.applicationId());
		print("software version", header.sqliteVersion());

		final var schema = database.schema();
		print("number of tables", schema.tables().size());
	}

	private static void print(String key, Object value) {
		final var format = "%-20s %s%n";
		System.out.printf(format, key + ":", value);
	}

	public static void dotTables(Database database) {
		final var names = database.schema()
			.tables()
			.stream()
			.map(Table::name)
			.collect(Collectors.joining(" "));
		
		System.out.println(names);
	}

}