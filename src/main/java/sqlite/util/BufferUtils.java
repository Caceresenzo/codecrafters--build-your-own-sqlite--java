package sqlite.util;

import java.nio.ByteBuffer;

public class BufferUtils {

	public static final int CONTINUE = 0b1000_0000;
	public static final int MASK = ~CONTINUE;

	public static long getVariableLength(ByteBuffer buffer) {
		var result = 0l;

		for (var index = 0; index < 8; index++) {
			final var current = Byte.toUnsignedInt(buffer.get());
			result = (result << 7) + (current & MASK);

			if ((current & CONTINUE) == 0) {
				return result;
			}
		}

		final var last = Byte.toUnsignedInt(buffer.get());
		result = (result << 8) + last;

		return result;
	}

	public static byte[] getN(ByteBuffer buffer, int n) {
		final var bytes = new byte[n];
		buffer.get(bytes);

		return bytes;
	}

	public static byte[] getN(ByteBuffer buffer, long n) {
		return getN(buffer, Math.toIntExact(n));
	}

	public static long getUnsignedInt(ByteBuffer buffer) {
		return Integer.toUnsignedLong(buffer.getInt());
	}

	public static int getUnsignedShort(ByteBuffer buffer) {
		return Short.toUnsignedInt(buffer.getShort());
	}

	public static int getUnsigned(ByteBuffer buffer) {
		return Byte.toUnsignedInt(buffer.get());
	}

	public static void skip(ByteBuffer buffer, int n) {
		buffer.position(buffer.position() + n);
	}

}