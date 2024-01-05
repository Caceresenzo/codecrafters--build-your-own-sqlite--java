package sqlite.domain.query;

import java.util.Iterator;

import sqlite.domain.Database;
import sqlite.domain.TableRow;

public interface Query {

	Iterator<TableRow> execute(Database database);
	
}