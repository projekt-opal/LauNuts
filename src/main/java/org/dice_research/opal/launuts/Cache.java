package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Data access.
 * 
 * @author Adrian Wilke
 */
public abstract class Cache extends Serialization {

	/**
	 * Writes data to cache file.
	 */
	protected static void writeLau(Cfg cfg, Map<String, List<LauContainer>> lau) throws IOException {
		write(lau, Files.getFileCached(cfg, Files.CACHE_LAU));
	}

	/**
	 * Reads data from cache file.
	 */
	@SuppressWarnings("unchecked")
	protected static Map<String, List<LauContainer>> readLau(Cfg cfg) throws ClassNotFoundException, IOException {
		File file = Files.getFileCached(cfg, Files.CACHE_LAU);
		if (file.canRead()) {
			return (TreeMap<String, List<LauContainer>>) Serialization.read(file);
		} else {
			return null;
		}
	}

	/**
	 * Writes data to cache file.
	 */
	protected static void writeLauGeo(Cfg cfg, Map<String, Map<String, PolygonContainer>> lauGeo) throws IOException {
		write(lauGeo, Files.getFileCached(cfg, Files.CACHE_LAU_GEO));
	}

	/**
	 * Reads data from cache file.
	 */
	@SuppressWarnings("unchecked")
	protected static Map<String, Map<String, PolygonContainer>> readLauGeo(Cfg cfg)
			throws ClassNotFoundException, IOException {
		File file = Files.getFileCached(cfg, Files.CACHE_LAU_GEO);
		if (file.canRead()) {
			return (Map<String, Map<String, PolygonContainer>>) Serialization.read(file);
		} else {
			return null;
		}
	}

}