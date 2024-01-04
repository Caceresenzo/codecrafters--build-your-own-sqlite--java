package sqlite.domain;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TextEncoding {

	UTF_8(1, StandardCharsets.UTF_8),
	UTF_16LE(2, StandardCharsets.UTF_16LE),
	UTF_16BE(3, StandardCharsets.UTF_16BE);

	private final int value;
	private final Charset charset;

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