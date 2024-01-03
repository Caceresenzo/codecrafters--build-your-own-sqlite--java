package sqlite.domain;

import java.nio.ByteBuffer;

public record Database(
	DatabaseHeader header,
	Schema schema,
	ByteBuffer buffer
) {}