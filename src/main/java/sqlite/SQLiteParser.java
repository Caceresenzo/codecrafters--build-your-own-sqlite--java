package sqlite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import sqlite.domain.Cell;
import sqlite.domain.Database;
import sqlite.domain.DatabaseHeader;
import sqlite.domain.FileFormatVersion;
import sqlite.domain.Page;
import sqlite.domain.PageHeader;
import sqlite.domain.PageType;
import sqlite.domain.Schema;
import sqlite.domain.TextEncoding;
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
		final var page = parsePage(buffer, databaseHeader, 1);

		return new Schema(
			page.cells().size()
		);
	}

	public static Page parsePage(ByteBuffer buffer, DatabaseHeader databaseHeader, int number) {
		if (number < 1) {
			throw new IllegalStateException("number < 1: " + number);
		}

		buffer = slicePageBuffer(buffer, databaseHeader.pageSize(), number);

		final var header = parsePageHeader(buffer);
		final var numberOfCells = header.numberOfCells();

		final var cellOffsets = new ArrayList<Integer>(numberOfCells);
		for (var index = 0; index < numberOfCells; ++index) {
			cellOffsets.add(BufferUtils.getUnsignedShort(buffer));
		}

		final var cells = new ArrayList<Cell>();
		for (final var offset : cellOffsets) {
			buffer.position(offset);

			final var cell = header.type().readCell(buffer);
			cells.add(cell);
		}

		return new Page(
			header,
			Collections.unmodifiableList(cells)
		);
	}

	private static PageHeader parsePageHeader(ByteBuffer buffer) {
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

}