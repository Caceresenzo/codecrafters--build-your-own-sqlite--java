package sqlite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sqlite.domain.Batch;
import sqlite.domain.Cell;
import sqlite.domain.Database;
import sqlite.domain.DatabaseHeader;
import sqlite.domain.FileFormatVersion;
import sqlite.domain.Page;
import sqlite.domain.PageHeader;
import sqlite.domain.PageType;
import sqlite.domain.Row;
import sqlite.domain.TextEncoding;
import sqlite.domain.schema.Schema;
import sqlite.domain.schema.Table;
import sqlite.domain.type.BlobType;
import sqlite.domain.type.BooleanType;
import sqlite.domain.type.DoubleType;
import sqlite.domain.type.IntegerType;
import sqlite.domain.type.NullType;
import sqlite.domain.type.StringType;
import sqlite.domain.type.Type;
import sqlite.util.BufferUtils;

public class SQLiteParser {

	public static final int HEADER_SIZE = 100;

	public static Database parse(Path path) throws IOException {
		return parse(
			ByteBuffer
				.wrap(Files.readAllBytes(path))
				.asReadOnlyBuffer()
		);
	}

	public static Database parse(ByteBuffer buffer) {
		final var header = parseDatabaseHeader(buffer.slice(0, HEADER_SIZE));
		final var schema = parseSchema(buffer, header);

		return new Database(
			header,
			schema,
			buffer
		);
	}

	public static ByteBuffer slicePageBuffer(ByteBuffer buffer, int pageSize, int pageNumber) {
		final var offset = (pageNumber - 1) * pageSize;

		return buffer.slice(HEADER_SIZE + offset, pageSize);
	}

	public static DatabaseHeader parseDatabaseHeader(ByteBuffer buffer) {
		final var headerStringBytes = new byte[16];
		buffer.get(headerStringBytes);

		final var headerString = new String(headerStringBytes);
		if (!"SQLite format 3\0".equals(headerString)) {
			throw new IllegalStateException("invalid header string: " + headerString);
		}

		final var pageSize = BufferUtils.getUnsignedShort(buffer);
		final var writeVersion = FileFormatVersion.valueOf(buffer.get());
		final var readVersion = FileFormatVersion.valueOf(buffer.get());
		final var reservedBytes = BufferUtils.getUnsigned(buffer);
		final var maximumEmbeddedPayloadFraction = BufferUtils.getUnsigned(buffer);
		final var minimumEmbeddedPayloadFraction = BufferUtils.getUnsigned(buffer);
		final var leafPayloadFraction = BufferUtils.getUnsigned(buffer);
		final var fileChangeCounter = BufferUtils.getUnsignedInt(buffer);
		final var pageCount = BufferUtils.getUnsignedInt(buffer);
		final var firstFreelistPageNumber = BufferUtils.getUnsignedInt(buffer);
		final var freelistPageCount = BufferUtils.getUnsignedInt(buffer);
		final var schemaCookie = BufferUtils.getUnsignedInt(buffer);
		final var schemaFormat = BufferUtils.getUnsignedInt(buffer);
		final var defaultPageCacheSize = BufferUtils.getUnsignedInt(buffer);
		final var autovacuumTopRoot = BufferUtils.getUnsignedInt(buffer);
		final var textEncoding = TextEncoding.valueOf(buffer.getInt());
		final var userVersion = BufferUtils.getUnsignedInt(buffer);
		final var incrementalVacuum = BufferUtils.getUnsignedInt(buffer);
		final var applicationId = BufferUtils.getUnsignedInt(buffer);

		BufferUtils.skip(buffer, 20);

		final var versionValidFor = BufferUtils.getUnsignedInt(buffer);
		final var sqliteVersion = BufferUtils.getUnsignedInt(buffer);

		return new DatabaseHeader(
			pageSize,
			writeVersion,
			readVersion,
			reservedBytes,
			maximumEmbeddedPayloadFraction,
			minimumEmbeddedPayloadFraction,
			leafPayloadFraction,
			fileChangeCounter,
			pageCount,
			firstFreelistPageNumber,
			freelistPageCount,
			schemaCookie,
			schemaFormat,
			defaultPageCacheSize,
			autovacuumTopRoot,
			textEncoding,
			userVersion,
			incrementalVacuum,
			applicationId,
			versionValidFor,
			sqliteVersion
		);
	}

	public static Schema parseSchema(ByteBuffer buffer, DatabaseHeader databaseHeader) {
		final var page = parsePage(buffer, databaseHeader.pageSize(), 1);

		final var tables = new ArrayList<Table>();

		final var textEncoding = databaseHeader.textEncoding();
		final var batches = page.cells()
			.stream()
			.filter(Cell.WithPayload.class::isInstance)
			.map((cell) -> parseBatch((Cell.WithPayload) cell, textEncoding))
			.toList();

		for (final var batch : batches) {
			for (final var row : batch.rows()) {
				final var type = row.get(0);

				if ("table".equals(type)) {
					final var name = row.getString(1);
					final var rootPage = row.getLong(3);

					tables.add(new Table(
						name,
						rootPage
					));
				}
			}
		}

		return new Schema(
			tables
		);
	}

	public static Page parsePage(ByteBuffer buffer, int pageSize, int number) {
		if (number < 1) {
			throw new IllegalStateException("number < 1: " + number);
		}

		final var pageBuffer = slicePageBuffer(buffer, pageSize, number);

		return parsePage(
			pageBuffer
		);
	}

	public static Page parsePage(ByteBuffer buffer) {
		final var header = parsePageHeader(buffer);
		final var numberOfCells = header.numberOfCells();

		final var cellOffsets = new ArrayList<Integer>(numberOfCells);
		for (var index = 0; index < numberOfCells; ++index) {
			cellOffsets.add(BufferUtils.getUnsignedShort(buffer));
		}

		final var cells = new ArrayList<Cell>();
		for (final var offset : cellOffsets) {
			/* TODO only for first page? */
			buffer.position(offset - HEADER_SIZE);

			final var cell = header.type().readCell(buffer);
			cells.add(cell);
		}

		return new Page(
			header,
			Collections.unmodifiableList(cells)
		);
	}

	public static PageHeader parsePageHeader(ByteBuffer buffer) {
		final var pageType = PageType.valueOf(buffer.get());
		final var firstFreeBlockOffset = BufferUtils.getUnsignedShort(buffer);
		final var numberOfCells = BufferUtils.getUnsignedShort(buffer);

		var cellContentAreaOffset = BufferUtils.getUnsignedShort(buffer);
		if (cellContentAreaOffset == 0) {
			cellContentAreaOffset = 65536;
		}

		final var numberOfFragmentedFreeBytes = BufferUtils.getUnsigned(buffer);
		final var rightMost = pageType.hasRightMost() ? BufferUtils.getUnsignedInt(buffer) : 0;

		return new PageHeader(
			pageType,
			firstFreeBlockOffset,
			numberOfCells,
			cellContentAreaOffset,
			numberOfFragmentedFreeBytes,
			rightMost
		);
	}

	public static Batch parseBatch(Cell.WithPayload cell, TextEncoding textEncoding) {
		final var id = cell instanceof Cell.WithRowId withRowId
			? withRowId.rowId()
			: -1;

		final var buffer = ByteBuffer.wrap(cell.payload()).asReadOnlyBuffer();
		final var rows = parseRows(buffer, textEncoding);

		return new Batch(
			id,
			Collections.unmodifiableList(rows)
		);
	}

	public static List<Row> parseRows(ByteBuffer buffer, TextEncoding textEncoding) {
		final var rows = new ArrayList<Row>();

		final var columnTypes = parseColumnTypes(buffer, textEncoding);
		while (buffer.hasRemaining()) {
			final var values = new ArrayList<>(columnTypes.size());

			for (final var columnType : columnTypes) {
				final var value = columnType.parseValue(buffer);
				values.add(value);
			}

			rows.add(new Row(values));
		}

		return rows;
	}

	private static List<Type> parseColumnTypes(ByteBuffer buffer, TextEncoding textEncoding) {
		final var columnTypes = new ArrayList<Type>();

		final var headerSize = BufferUtils.getVariableLength(buffer);
		while (buffer.position() < headerSize) {
			final var typeId = BufferUtils.getVariableLength(buffer);

			final var columnType = parseColumnType(typeId, textEncoding);
			columnTypes.add(columnType);
		}

		return columnTypes;
	}

	private static Type parseColumnType(long typeId, TextEncoding textEncoding) {
		if (typeId == 0) {
			return NullType.INSTANCE;
		}

		if (typeId == 1) {
			return IntegerType.SIZE_8;
		}

		if (typeId == 2) {
			return IntegerType.SIZE_16;
		}

		if (typeId == 3) {
			return IntegerType.SIZE_24;
		}

		if (typeId == 4) {
			return IntegerType.SIZE_32;
		}

		if (typeId == 5) {
			return IntegerType.SIZE_48;
		}

		if (typeId == 6) {
			return IntegerType.SIZE_64;
		}

		if (typeId == 7) {
			return DoubleType.INSTANCE;
		}

		if (typeId == 8) {
			return BooleanType.FALSE;
		}

		if (typeId == 9) {
			return BooleanType.TRUE;
		}

		if (typeId == 10) {
			throw new UnsupportedOperationException("internal 10");
		}

		if (typeId == 11) {
			throw new UnsupportedOperationException("internal 11");
		}

		if (typeId % 2 == 0) {
			final var size = ((typeId - 12) / 2);
			return new BlobType(size);
		}

		final var size = ((typeId - 13) / 2);
		return new StringType(size, textEncoding);
	}

}