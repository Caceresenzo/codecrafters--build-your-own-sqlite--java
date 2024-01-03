package sqlite.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum FileFormatVersion {

	LEGACY(1),
	WAL(2);
	
	private final int value;

	public static FileFormatVersion valueOf(int value) {
		for (final var version : values()) {
			if (version.value() == value) {
				return version;
			}
		}

		throw new IllegalArgumentException("unknown file format version: " + value);
	}
	
}