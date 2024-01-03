package sqlite.domain;

public record DatabaseHeader(
	int pageSize,
	FileFormatVersion writeVersion,
	FileFormatVersion readVersion,
	int reservedBytes,
	int maximumEmbeddedPayloadFraction,
	int minimumEmbeddedPayloadFraction,
	int leafPayloadFraction,
	long fileChangeCounter,
	long pageCount,
	long firstFreelistPageNumber,
	long freelistPageCount,
	long schemaCookie,
	long schemaFormat,
	long defaultPageCacheSize,
	long autovacuumTopRoot,
	TextEncoding textEncoding,
	long userVersion,
	long incrementalVacuum,
	long applicationId,
	/* padding of 20 */
	long versionValidFor,
	long sqliteVersion
) {}