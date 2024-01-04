package sqlite.domain.type;

import java.nio.ByteBuffer;

public enum NullType implements Type {

	INSTANCE;

	@Override
	public long contentSize() {
		return 0;
	}

	@Override
	public Object parseValue(ByteBuffer buffer) {
		return null;
	}

}