package sqlite.domain.type;

import java.nio.ByteBuffer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BooleanType implements Type {

	FALSE(false),
	TRUE(true);
	
	private final boolean value;

	@Override
	public long contentSize() {
		return 0;
	}

	@Override
	public Object parseValue(ByteBuffer buffer) {
		return value;
	}
	
	@Override
	public String toString() {
		return "BooleanType.".formatted(name());
	}

}