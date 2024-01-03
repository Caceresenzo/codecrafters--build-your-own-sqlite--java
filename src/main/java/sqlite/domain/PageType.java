package sqlite.domain;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import sqlite.domain.Cell.LeafTable;
import sqlite.util.BufferUtils;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum PageType {

	INTERIOR_INDEX(0x02, true) {

		@Override
		public Cell readCell(ByteBuffer buffer) {
			final var leftChild = BufferUtils.getUnsignedInt(buffer);
			final var payloadSize = BufferUtils.getVariableLength(buffer);
			final var payload = BufferUtils.getN(buffer, payloadSize);

			return new Cell.InteriorIndex(leftChild, payload);
		}

	},

	INTERIOR_TABLE(0x05, true) {

		@Override
		public Cell readCell(ByteBuffer buffer) {
			final var leftChild = BufferUtils.getUnsignedInt(buffer);
			final var rowId = BufferUtils.getVariableLength(buffer);

			return new Cell.InteriorTable(leftChild, rowId);
		}

	},

	LEAF_INDEX(0x0a, false) {

		@Override
		public Cell readCell(ByteBuffer buffer) {
			final var payloadSize = BufferUtils.getVariableLength(buffer);
			final var payload = BufferUtils.getN(buffer, payloadSize);

			return new Cell.LeafIndex(payload);
		}

	},

	LEAF_TABLE(0x0d, false) {

		@Override
		public LeafTable readCell(ByteBuffer buffer) {
			final var payloadSize = BufferUtils.getVariableLength(buffer);
			final var rowId = BufferUtils.getVariableLength(buffer);
			final var payload = BufferUtils.getN(buffer, payloadSize);

			return new Cell.LeafTable(rowId, payload);
		}

	};

	private final int value;
	private final boolean hasRightMost;

	public abstract Cell readCell(ByteBuffer buffer);

	public static PageType valueOf(int value) {
		for (final var type : values()) {
			if (type.value() == value) {
				return type;
			}
		}

		throw new IllegalArgumentException("unknown page type: " + value);
	}

}