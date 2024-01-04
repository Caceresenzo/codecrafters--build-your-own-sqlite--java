package sqlite.domain.type;

import java.nio.ByteBuffer;

public enum DoubleType implements Type {

	INSTANCE;

	@Override
	public long contentSize() {
		return 8;
	}

	@Override
	public Object parseValue(ByteBuffer buffer) {
		return buffer.getDouble();
	}
	
	@Override
	public String toString() {
		return "DoubleType";
	}

}