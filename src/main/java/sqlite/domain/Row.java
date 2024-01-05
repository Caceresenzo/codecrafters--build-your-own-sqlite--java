package sqlite.domain;

import java.util.List;

public interface Row {
	
	long id();
	
	Object get(int index);
	
	List<Object> values();

	default String getString(int index) {
		return String.valueOf(get(index));
	}

	default int getInteger(int index) {
		return ((Number) get(index)).intValue();
	}

	default long getLong(int index) {
		return ((Number) get(index)).longValue();
	}

}