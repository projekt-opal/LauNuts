package org.dice_research.opal.launuts;

import java.util.Iterator;
import java.util.List;

import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;

/**
 * Performs SPARQL query at DBpedia and creates container objects.
 * 
 * @author Adrian Wilke
 */
public class DbpediaTest {

	public static void main(String[] args) throws Exception {

		// Config
		boolean deleteCache = false;

		boolean requestDbpediaData = true;
		boolean useCache = true;

		DbpediaTest dbpedia = new DbpediaTest();

		// Delete cache
		if (deleteCache) {
			dbpedia.deleteCache();
		}

		// Get data
		if (requestDbpediaData) {
			List<DbpediaPlaceContainer> containers = dbpedia.requestData(useCache);
			System.out.println();

			System.out.println("Example:");
			Iterator<DbpediaPlaceContainer> it = containers.iterator();
			if (it.hasNext()) {
				System.out.println(it.next());
			}
		}

	}

	List<DbpediaPlaceContainer> requestData(boolean useCache) throws Exception {
		List<DbpediaPlaceContainer> containers = Cache.getDbpedia(useCache);
		System.out.println("Got " + containers.size() + " entries");
		return containers;
	}

	void deleteCache() {
		System.out.println("DBpedia cache: " + Cache.FILE_DBPEDIA.getAbsolutePath());
		if (Cache.FILE_DBPEDIA.exists()) {
			if (Cache.FILE_DBPEDIA.delete()) {
				System.out.println("Deleted DBpedia cache.");
			} else {
				System.out.println("Could not delete DBpedia cache.");
			}
		} else {
			System.out.println("DBpedia cache file does not exist.");
		}
	}
	
}