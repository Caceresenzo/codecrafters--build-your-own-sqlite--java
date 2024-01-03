package sqlite.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TextEncoding {

	UTF_8(1),
	UTF_16LE(2),
	UTF_16BE(3);

	private final int value;

	public String format() {
		return "%d (%s)".formatted(value, name().replace("_", "-").toLowerCase());
	}

	public static TextEncoding valueOf(int value) {
		for (final var encoding : values()) {
			if (encoding.value() == value) {
				return encoding;
			}
		}

		throw new IllegalArgumentException("invalid text encoding: " + value);
	}

}