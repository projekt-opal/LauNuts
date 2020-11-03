package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration based on property files.
 *
 * @author Adrian Wilke
 */
public class Cfg {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String DEFAULT_CONFIGURATION_FILE = "default.properties";
	private Properties properties;

	public Cfg() {
		this(new File(DEFAULT_CONFIGURATION_FILE));
	}

	public Cfg(File file) {
		LOGGER.info("Configuration: " + file.getAbsolutePath());
		if (!file.canRead()) {
			throw new RuntimeException("Can not read configuration file: " + file.getAbsolutePath());
		}

		properties = new Properties();
		try {
			properties.load(new FileReader(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String get(String key) {
		return properties.getProperty(key).trim();
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(properties.getProperty(key).trim());
	}

	public long getLong(String key) {
		return Long.parseLong(properties.getProperty(key).trim());
	}

	public int getInt(String key) {
		return Integer.parseInt(properties.getProperty(key).trim());
	}

	public List<String> getKeys(String prefix) {
		List<String> keys = new LinkedList<>();
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = propertyNames.nextElement().toString();
			if (propertyName.startsWith(prefix)) {
				keys.add(propertyName);
			}
		}
		return keys;
	}

	public boolean has(String key) {
		return properties.containsKey(key) && !properties.getProperty(key).trim().isEmpty();
	}

	public Cfg set(String key, String value) {
		properties.setProperty(key, value);
		return this;
	}

	@Override
	public String toString() {
		int maxlength = 0;
		for (Object key : properties.keySet()) {
			if (key.toString().length() > maxlength) {
				maxlength = key.toString().length();
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Object key : new TreeSet<>(properties.keySet())) {
			stringBuilder.append(key.toString());
			for (int i = 0; i <= maxlength - key.toString().length(); i++) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(properties.get(key));
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}
}