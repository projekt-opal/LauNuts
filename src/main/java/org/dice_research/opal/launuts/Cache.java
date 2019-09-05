package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dice_research.opal.launuts.utils.Serialization;

/**
 * Data access.
 * 
 * @author Adrian Wilke
 */
public abstract class Cache extends Serialization {

	public static final File FILE_DBPEDIA = new File(Cfg.getInstance().get(Cfg.CACHE_DIRECTORY), "dbpedia");
	public static final File FILE_LAU = new File(Cfg.getInstance().get(Cfg.CACHE_DIRECTORY), "lau");
	public static final File FILE_NUTS = new File(Cfg.getInstance().get(Cfg.CACHE_DIRECTORY), "nuts");

	public static List<DbpediaPlaceContainer> getDbpedia(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_DBPEDIA.exists()) {
			System.out.println("Reading DBPEDIA from cache.");
			return Cache.readDbpedia();
		} else {
			System.out.println("Computing DBPEDIA.");

			DbpediaRemote dbpediaRemote = new DbpediaRemote();
			if (useCache) {
				Cache.writeDbpedia(dbpediaRemote.getPlaces());
			}
			return dbpediaRemote.getPlaces();
		}
	}

	public static List<LauContainer> getLau(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_LAU.exists()) {
			System.out.println("Reading LAU from cache.");
			return Cache.readLau();
		} else {
			System.out.println("Computing LAU.");
			LauCsvParser lauCsvParser = new LauCsvParser().parse(Cfg.getInstance().get(Cfg.LAU_FILE));
			if (useCache) {
				Cache.writeLau(lauCsvParser.getLauList());
			}
			return lauCsvParser.getLauList();
		}
	}

	public static Map<String, NutsContainer> getNuts(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_NUTS.exists()) {
			System.out.println("Reading NUTS from cache.");
			return Cache.readNuts();
		} else {
			System.out.println("Computing NUTS.");
			NutsRdfExtractor nutsRdfExtractor = new NutsRdfExtractor(Cfg.getInstance().get(Cfg.NUTS_FILE))
					.extractNuts();
			if (useCache) {
				Cache.writeNuts(nutsRdfExtractor.getNutsIndex());
			}
			return nutsRdfExtractor.getNutsIndex();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<DbpediaPlaceContainer> readDbpedia() throws ClassNotFoundException, IOException {
		return (List<DbpediaPlaceContainer>) Serialization.read(FILE_DBPEDIA);
	}

	@SuppressWarnings("unchecked")
	public static List<LauContainer> readLau() throws ClassNotFoundException, IOException {
		return (List<LauContainer>) Serialization.read(FILE_LAU);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, NutsContainer> readNuts() throws ClassNotFoundException, IOException {
		return (Map<String, NutsContainer>) Serialization.read(FILE_NUTS);
	}

	public static void writeDbpedia(List<DbpediaPlaceContainer> dbpedia) throws IOException {
		FILE_DBPEDIA.getParentFile().mkdirs();
		write(dbpedia, FILE_DBPEDIA);
	}

	public static void writeLau(List<LauContainer> lau) throws IOException {
		FILE_LAU.getParentFile().mkdirs();
		write(lau, FILE_LAU);
	}

	public static void writeNuts(Map<String, NutsContainer> nuts) throws IOException {
		FILE_NUTS.getParentFile().mkdirs();
		write(nuts, FILE_NUTS);
	}
}