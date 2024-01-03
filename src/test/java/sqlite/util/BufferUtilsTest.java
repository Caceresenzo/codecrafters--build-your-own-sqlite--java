package sqlite.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class BufferUtilsTest {

	@Test
	void getVariableLength() {
		assertEquals(127, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b0111_1111
		})));

		assertEquals(16383, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));

		assertEquals(2097151, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));

		assertEquals(268435455, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));
		
		assertEquals(34359738367l, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));
		
		assertEquals(4398046511103l, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));
		
		assertEquals(562949953421311l, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));
		
		assertEquals(72057594037927935l, BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b0111_1111,
		})));
		
		assertEquals("18446744073709551615", Long.toUnsignedString(BufferUtils.getVariableLength(ByteBuffer.wrap(new byte[] {
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
			(byte) 0b1111_1111,
		}))));
	}

}
