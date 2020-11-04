package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main entry point.
 * 
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * Version from pom.xml
	 */
	public static final String VERSION = "0.4.0-SNAPSHOT";

	private static final Logger LOGGER = LogManager.getLogger();
	private Cfg cfg;

	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	private void run() throws Exception {

		// Load configuration, download data
		initialize();

		// Read NUTS

		Model nutsModel = RDFDataMgr.loadModel(Files.getFileDownloaded(cfg, Files.NUTS_RDF_LOCAL).toURI().toString());
		LOGGER.info("Loaded NUTS RDF, size: " + nutsModel.size());

		// Extract LAU

		Map<String, List<LauContainer>> lau = Cache.readLau(cfg);
		if (null == lau) {
			File lauXslxFile = Files.getFileDownloaded(cfg, Files.LAU_2017_XLSX_LOCAL);
			lau = new LauXslsReader(lauXslxFile).extract().getResults();
			Cache.writeLau(cfg, lau);
			LOGGER.info("Extracted LAU data");
		} else {
			LOGGER.info("Loaded extracted LAU data from cache.");
		}

		// Build model

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.addNuts(nutsModel);
		modelBuilder.addLau(lau);
		LOGGER.info("Created model, size: " + modelBuilder.getModel().size());

		File outFile = Files.getFileCached(cfg, Files.FINAL_MODEL);
		RDFDataMgr.write(new FileOutputStream(outFile), modelBuilder.getModel(), Lang.TURTLE);
		LOGGER.info("Wrote file: " + outFile.getAbsolutePath());

	}

	/**
	 * Loads configuration.
	 * 
	 * Downloads files.
	 */
	private void initialize() {

		// Initialize configuration
		cfg = new Cfg();
		LOGGER.info("Internal cache: " + new File(cfg.get(CfgKeys.ioCacheDirectory)).getAbsolutePath());
		LOGGER.info("Downloads: " + new File(cfg.get(CfgKeys.ioDownloadDirectory)).getAbsolutePath());

		// Get data
		download(Files.NUTS_RDF, Files.NUTS_RDF_LOCAL);
		download(Files.LAU_2017_XLSX, Files.LAU_2017_XLSX_LOCAL);
	}

	/**
	 * Downloads file, if not locally available.
	 */
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