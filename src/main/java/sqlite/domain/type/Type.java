package sqlite.domain.type;

import java.nio.ByteBuffer;

public interface Type {

	long contentSize();

	Object parseValue(ByteBuffer buffer);

}