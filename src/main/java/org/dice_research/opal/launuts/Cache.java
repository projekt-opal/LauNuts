package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.dbpedia.DbpediaRemote;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.dice_research.opal.launuts.lau.LauCsvParser;
import org.dice_research.opal.launuts.nuts.NutsContainer;
import org.dice_research.opal.launuts.nuts.NutsRdfExtractor;
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

	/**
	 * Reads data from cache, if {@code useCache} is set and cache-file exists.
	 * 
	 * If no cache available, {@link DbpediaRemote#getPlaces()} is used.
	 */
	public static List<DbpediaPlaceContainer> getDbpedia(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_DBPEDIA.exists()) {
			List<DbpediaPlaceContainer> dbpedia = Cache.readDbpedia();
			System.out.println("Read " + dbpedia.size() + " DBPEDIA from cache.");
			return dbpedia;
		} else {
			System.out.println("Computing DBPEDIA.");
			DbpediaRemote dbpediaRemote = new DbpediaRemote();
			if (useCache) {
				Cache.writeDbpedia(dbpediaRemote.getPlaces());
			}
			return dbpediaRemote.getPlaces();
		}
	}

	/**
	 * Reads data from cache, if {@code useCache} is set and cache-file exists.
	 * 
	 * If no cache available, {@link LauCsvParser} is used.
	 */
	public static List<LauContainer> getLau(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_LAU.exists()) {
			List<LauContainer> lau = Cache.readLau();
			System.out.println("Read " + lau.size() + " LAU from cache.");
			return lau;
		} else {
			System.out.println("Computing LAU.");
			LauCsvParser lauCsvParser = new LauCsvParser().parse(Cfg.getInstance().get(Cfg.LAU_FILE));
			if (useCache) {
				Cache.writeLau(lauCsvParser.getLauList());
			}
			return lauCsvParser.getLauList();
		}
	}

	/**
	 * Reads data from cache, if {@code useCache} is set and cache-file exists.
	 * 
	 * If no cache available, {@link NutsRdfExtractor} is used.
	 */
	public static Map<String, NutsContainer> getNuts(boolean useCache) throws Exception {
		if (useCache && Cache.FILE_NUTS.exists()) {
			Map<String, NutsContainer> nuts = Cache.readNuts();
			System.out.println("Read " + nuts.size() + " NUTS from cache.");
			return nuts;
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

	/**
	 * Reads data from cache file.
	 */
	@SuppressWarnings("unchecked")
	protected static List<DbpediaPlaceContainer> readDbpedia() throws ClassNotFoundException, IOException {
		return (List<DbpediaPlaceContainer>) Serialization.read(FILE_DBPEDIA);
	}

	/**
	 * Reads data from cache file.
	 */
	@SuppressWarnings("unchecked")
	protected static List<LauContainer> readLau() throws ClassNotFoundException, IOException {
		return (List<LauContainer>) Serialization.read(FILE_LAU);
	}

	@SuppressWarnings("unchecked")
	protected static Map<String, NutsContainer> readNuts() throws ClassNotFoundException, IOException {
		return (Map<String, NutsContainer>) Serialization.read(FILE_NUTS);
	}

	/**
	 * Writes data to cache file.
	 */
	protected static void writeDbpedia(List<DbpediaPlaceContainer> dbpedia) throws IOException {
		FILE_DBPEDIA.getParentFile().mkdirs();
		write(dbpedia, FILE_DBPEDIA);
	}

	/**
	 * Writes data to cache file.
	 */
	protected static void writeLau(List<LauContainer> lau) throws IOException {
		FILE_LAU.getParentFile().mkdirs();
		write(lau, FILE_LAU);
	}

	/**
	 * Writes data to cache file.
	 */
	protected static void writeNuts(Map<String, NutsContainer> nuts) throws IOException {
		FILE_NUTS.getParentFile().mkdirs();
		write(nuts, FILE_NUTS);
	}
}