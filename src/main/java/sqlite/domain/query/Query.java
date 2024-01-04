package sqlite.domain.query;

import java.util.Iterator;

import sqlite.domain.Database;
import sqlite.domain.Row;

public interface Query {

	Iterator<Row> execute(Database database);
	
}