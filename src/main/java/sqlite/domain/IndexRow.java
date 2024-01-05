package sqlite.domain;

import java.util.List;

public record IndexRow(
	List<Object> values
) implements Row {

	@Override
	public long id() {
		return getLong(values.size() - 1);
	}

	@Override
	public Object get(int index) {
		return values.get(index);
	}

}