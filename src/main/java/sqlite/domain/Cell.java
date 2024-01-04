package sqlite.domain;

import sqlite.domain.Cell.InteriorIndex;
import sqlite.domain.Cell.InteriorTable;
import sqlite.domain.Cell.LeafIndex;
import sqlite.domain.Cell.LeafTable;
import sqlite.domain.Cell.WithPayload;
import sqlite.domain.Cell.WithRowId;

public sealed interface Cell permits InteriorIndex, InteriorTable, LeafIndex, LeafTable, WithPayload, WithRowId {

	public static sealed interface WithPayload extends Cell permits InteriorIndex, LeafIndex, LeafTable {

		byte[] payload();

	}

	public static sealed interface WithRowId extends Cell permits InteriorTable, LeafTable {

		long rowId();

	}

	public static record InteriorIndex(
		long leftChild,
		byte[] payload
	) implements Cell, WithPayload {}

	public static record InteriorTable(
		long leftChild,
		long rowId
	) implements Cell, WithRowId {}

	public static record LeafIndex(
		byte[] payload
	) implements Cell, WithPayload {}

	public static record LeafTable(
		long rowId,
		byte[] payload
	) implements Cell, WithPayload, WithRowId {}

}