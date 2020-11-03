package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main entry point.
 * 
 * @author Adrian Wilke
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger();
	private Cfg cfg;

	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	private void run() throws Exception {

		// Initialize LauNuts
		initialize();

		// Extract NUTS
		Map<String, List<LauContainer>> lau = new Lau(cfg).extract();

		// TODO
		for (Entry<String, List<LauContainer>> sheet : lau.entrySet()) {
			System.out.println(sheet.getKey() + " " + sheet.getValue().size());
		}

	}

	private void initialize() {

		// Initialize configuration
		cfg = new Cfg();

		// Get data
		download(Files.NUTS_RDF, Files.NUTS_RDF_LOCAL);
		download(Files.LAU_2017_XLSX, Files.LAU_2017_XLSX_LOCAL);
	}

	private void download(String url, String filename) {
		File file = new File(cfg.get(CfgKeys.ioDownloadDirectory), filename);

		// Only log, if file exists
		if (file.exists()) {
			LOGGER.info("Local file found: " + file.getAbsolutePath());
			return;
		}

		// Download
		LOGGER.info("Downloading: " + url);
		try {
			FileUtils.copyURLToFile(new URL(url), file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("Downloaded: " + file.getAbsolutePath());
	}

}