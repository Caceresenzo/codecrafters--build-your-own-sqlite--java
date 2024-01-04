package sqlite.domain;

import java.nio.ByteBuffer;

import sqlite.domain.schema.Schema;

public record Database(
	DatabaseHeader header,
	Schema schema,
	ByteBuffer buffer
) {}