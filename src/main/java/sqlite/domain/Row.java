package sqlite.domain;

import java.util.List;

public record Row(
	List<Object> values
) {
	
	public Object get(int index) {
		return values.get(index);
	}
	
	public String getString(int index) {
		return String.valueOf(values.get(index));
	}
	
	public int getInteger(int index) {
		return ((Number) values.get(index)).intValue();
	}
	
	public long getLong(int index) {
		return ((Number) values.get(index)).longValue();
	}
	
}