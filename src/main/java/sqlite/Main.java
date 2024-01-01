package sqlite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Missing <database path> and <command>");
			return;
		}

		final var sqlitePath = args[0];
		final var buffer = ByteBuffer.wrap(Files.readAllBytes(Path.of(sqlitePath))).order(ByteOrder.BIG_ENDIAN);
		final var database = new Database(buffer);

		final var command = args[1];
		switch (command) {
			case ".dbinfo" -> dbinfo(database);
			default -> System.out.println("Missing or invalid command passed: " + command);
		}
	}

	public static void dbinfo(Database database) {
		print("database page size", database.getPageSize());
		print("write format", database.getWriteVersion());
		print("read format", database.getReadVersion());
		print("reserved bytes", database.getReservedBytes());
		print("file change counter", database.getFileChangeCounter());
		print("database page count", database.getPageCount());
		print("freelist page count", database.getFreelistPageCount());
		print("schema cookie", database.getSchemaCookie());
		print("schema format", database.getSchemaFormat());
		print("default cache size", database.getDefaultPageCacheSize());
		print("autovacuum top root", database.getAutovacuumTopRoot());
		print("incremental vacuum", database.getIncrementalVacuum());
		print("text encoding", database.getTextEncoding().format());
		print("user version", database.getUserVersion());
		print("application id", database.getApplicationId());
		print("software version", database.getSqliteVersion());
	}

	private static void print(String key, Object value) {
		final var format = "%-20s %s%n";
		System.out.printf(format, key + ":", value);
	}

}