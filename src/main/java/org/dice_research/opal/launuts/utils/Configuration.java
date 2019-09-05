package org.dice_research.opal.launuts.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Configuration in properties XML file.
 * 
 * @author Adrian Wilke
 */
public abstract class Configuration {

	private File file;
	private Properties properties;

	public Configuration(File file) {
		this.file = file;
		if (file.canRead()) {
			load();
		} else {
			properties = getDefaultProperties();
			store();
		}
	}

	abstract protected Properties getDefaultProperties();

	public String get(String key) {
		return properties.getProperty(key);
	}

	public void load() {
		properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Loaded configuration from " + file.getAbsolutePath());
		printConfiguration();
	}

	public void store() {
		try {
			properties.storeToXML(new FileOutputStream(file), "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Stored configuration to " + file.getAbsolutePath());
	}

	private void printConfiguration() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Object key : properties.keySet()) {
			stringBuilder.append(key);
			stringBuilder.append(": ");
			stringBuilder.append(properties.get(key));
			stringBuilder.append(System.lineSeparator());
		}
		System.out.println(stringBuilder.toString());
	}
}