package sqlite.domain.type;

import java.nio.ByteBuffer;

import sqlite.domain.TextEncoding;
import sqlite.util.BufferUtils;

public record StringType(
	long size,
	TextEncoding textEncoding
) implements Type {

	@Override
	public long contentSize() {
		return size;
	}

	@Override
	public Object parseValue(ByteBuffer buffer) {
		final var bytes = BufferUtils.getN(buffer, size);

		return new String(bytes, textEncoding.charset());
	}

}