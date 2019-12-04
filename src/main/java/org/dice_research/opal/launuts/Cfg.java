package org.dice_research.opal.launuts;

import java.io.File;
import java.util.Properties;

import org.dice_research.opal.launuts.utils.Configuration;

/**
 * Configuration.
 * 
 * If no configuration file is found, a new one is created.
 *
 * @author Adrian Wilke
 */
public class Cfg extends Configuration {

	public static final String CACHE_DIRECTORY = "directory.cache";
	public static final String OUT_DIRECTORY = "directory.output";
	public static final String LAU_FILE = "lau.csv.file";
	public static final String NUTS_FILE = "nuts.rdf.file";

	private static Cfg instance;

	private Cfg() {
		super(new File("launuts-configuration.xml"));
	}

	public static Cfg getInstance() {
		if (instance == null) {
			instance = new Cfg();
		}
		return instance;
	}

	@Override
	protected Properties getDefaultProperties() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		Properties properties = new Properties();
		properties.put(OUT_DIRECTORY, new File(tmpDir, "launuts").getAbsolutePath());
		properties.put(CACHE_DIRECTORY, new File(tmpDir, "launuts-cache").getAbsolutePath());
		properties.put(LAU_FILE, "EU-28-LAU-2019-NUTS-2016-DE.csv");
		properties.put(NUTS_FILE, "nuts.rdf");
		return properties;
	}

}