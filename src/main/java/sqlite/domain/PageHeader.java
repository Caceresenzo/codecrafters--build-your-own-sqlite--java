package sqlite.domain;

public record PageHeader(
	PageType type,
	int firstFreeBlockOffset,
	int numberOfCells,
	int cellContentAreaOffset,
	int numberOfFragmentedFreeBytes,
	long rightMost
) {}