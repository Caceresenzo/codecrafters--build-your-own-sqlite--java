package sqlite.domain;

import sqlite.domain.Cell.InteriorIndex;
import sqlite.domain.Cell.InteriorTable;
import sqlite.domain.Cell.LeafIndex;
import sqlite.domain.Cell.LeafTable;

public sealed interface Cell permits InteriorIndex, InteriorTable, LeafIndex, LeafTable {

	public static record InteriorIndex(
		long leftChild,
		byte[] payload
	) implements Cell {}

	public static record InteriorTable(
		long leftChild,
		long rowId
	) implements Cell {}

	public static record LeafIndex(
		byte[] payload
	) implements Cell {}

	public static record LeafTable(
		long rowId,
		byte[] payload
	) implements Cell {}

}