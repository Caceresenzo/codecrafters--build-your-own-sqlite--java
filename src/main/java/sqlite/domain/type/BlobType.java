package sqlite.domain.type;

import java.nio.ByteBuffer;

import sqlite.util.BufferUtils;

public record BlobType(
	long size
) implements Type {

	@Override
	public long contentSize() {
		return size;
	}

	@Override
	public Object parseValue(ByteBuffer buffer) {
		return BufferUtils.getN(buffer, size);
	}

}