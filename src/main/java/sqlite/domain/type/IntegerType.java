package sqlite.domain.type;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum IntegerType implements Type {

	SIZE_8(1) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			return buffer.get();
		}

	},
	SIZE_16(2) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			return buffer.getShort();
		}

	},
	SIZE_24(3) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			throw new UnsupportedOperationException("SIZE_24");
		}

	},
	SIZE_32(4) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			return buffer.getInt();
		}

	},
	SIZE_48(6) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			throw new UnsupportedOperationException("SIZE_48");
		}

	},
	SIZE_64(8) {

		@Override
		public Object parseValue(ByteBuffer buffer) {
			return buffer.getLong();
		}

	};

	private final long contentSize;

	@Override
	public abstract Object parseValue(ByteBuffer buffer);

	@Override
	public String toString() {
		return "IntegerType.%s".formatted(name());
	}

}