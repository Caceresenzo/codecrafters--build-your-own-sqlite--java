package sqlite.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class TableRow implements Row {

	private final long id;
	private final List<Object> values;
	private List<Integer> columnIndexes;

	@Override
	public Object get(int index) {
		return values.get(index);
	}

	public TableRow reIndex(List<Integer> columnIndexes) {
		final var newValues = new ArrayList<>();

		for (final var index : columnIndexes) {
			newValues.add(values.get(index));
		}

		return new TableRow(id, newValues, columnIndexes);
	}

	public List<Integer> getColumnIndexes() {
		if (columnIndexes == null) {
			columnIndexes = IntStream
				.range(0, values.size())
				.mapToObj((x) -> x)
				.toList();
		}

		return columnIndexes;
	}

}