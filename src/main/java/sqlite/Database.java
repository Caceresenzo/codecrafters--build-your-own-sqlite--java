package sqlite;

import java.nio.ByteBuffer;

import lombok.Getter;

@Getter
public class Database {

	private final ByteBuffer buffer;
	private final int pageSize;
	private final int writeVersion;
	private final int readVersion;
	private final int reservedBytes;
	private final int maximumEmbeddedPayloadFraction;
	private final int minimumEmbeddedPayloadFraction;
	private final int leafPayloadFraction;
	private final long fileChangeCounter;
	private final long pageCount;
	private final long firstFreelistPageNumber;
	private final long freelistPageCount;
	private final long schemaCookie;
	private final long schemaFormat;
	private final long defaultPageCacheSize;
	private final long autovacuumTopRoot;
	private final TextEncoding textEncoding;
	private final long userVersion;
	private final long incrementalVacuum;
	private final long applicationId;
	/* padding of 20 */
	private final long versionValidFor;
	private final long sqliteVersion;

	public Database(ByteBuffer buffer) {
		this.buffer = buffer;

		final var headerStringBytes = new byte[16];
		buffer.get(headerStringBytes);

		final var headerString = new String(headerStringBytes);
		if (!"SQLite format 3\0".equals(headerString)) {
			throw new IllegalStateException("invalid header string: " + headerString);
		}

		this.pageSize = Short.toUnsignedInt(buffer.getShort());
		this.writeVersion = Byte.toUnsignedInt(buffer.get());
		this.readVersion = Byte.toUnsignedInt(buffer.get());
		this.reservedBytes = Byte.toUnsignedInt(buffer.get());
		this.maximumEmbeddedPayloadFraction = Byte.toUnsignedInt(buffer.get());
		this.minimumEmbeddedPayloadFraction = Byte.toUnsignedInt(buffer.get());
		this.leafPayloadFraction = Byte.toUnsignedInt(buffer.get());
		this.fileChangeCounter = Integer.toUnsignedLong(buffer.getInt());
		this.pageCount = Integer.toUnsignedLong(buffer.getInt());
		this.firstFreelistPageNumber = Integer.toUnsignedLong(buffer.getInt());
		this.freelistPageCount = Integer.toUnsignedLong(buffer.getInt());
		this.schemaCookie = Integer.toUnsignedLong(buffer.getInt());
		this.schemaFormat = Integer.toUnsignedLong(buffer.getInt());
		this.defaultPageCacheSize = Integer.toUnsignedLong(buffer.getInt());
		this.autovacuumTopRoot = Integer.toUnsignedLong(buffer.getInt());
		this.textEncoding = TextEncoding.valueOf(buffer.getInt());
		this.userVersion = Integer.toUnsignedLong(buffer.getInt());
		this.incrementalVacuum = Integer.toUnsignedLong(buffer.getInt());
		this.applicationId = Integer.toUnsignedLong(buffer.getInt());

		buffer.position(buffer.position() + 20);

		this.versionValidFor = Integer.toUnsignedLong(buffer.getInt());
		this.sqliteVersion = Integer.toUnsignedLong(buffer.getInt());
	}

}