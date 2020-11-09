package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lingala.zip4j.ZipFile;

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

	public static final boolean STEP_1_NUTS = true;
	public static final boolean STEP_2_LAU = true;
	public static final boolean STEP_3_POINTS = true;
	public static final boolean STEP_4_RDF = true;

	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	private void run() throws Exception {

		// Load configuration
		initialize();

		// Download files
		download();

		// Read NUTS

		Model nutsModel = ModelFactory.createDefaultModel();
		if (STEP_1_NUTS) {
			nutsModel = RDFDataMgr.loadModel(Files.getFileDownloaded(cfg, Files.NUTS_RDF_LOCAL).toURI().toString());
			LOGGER.info("Loaded NUTS RDF, size: " + nutsModel.size());
		}

		// Read LAU

		Map<String, List<LauContainer>> lau = new HashMap<>();
		if (STEP_2_LAU) {
			lau = Cache.readLau(cfg);
			if (null == lau) {
				File lauXslxFile = Files.getFileDownloaded(cfg, Files.LAU_2017_XLSX_LOCAL);
				lau = new LauXslsReader(lauXslxFile).extract().getResults();
				Cache.writeLau(cfg, lau);
				LOGGER.info("Extracted LAU data, countries: " + lau.size());
			} else {
				LOGGER.info("Loaded extracted LAU data from cache, countries: " + lau.size());
			}
		}

		// Read geo data
		// Files description:
		// https://interactivetool.eu/EASME/EOCIC/map/geoJson/ref-nuts-2016-20m.geojson%20%289%29/release-notes.txt

		if (STEP_3_POINTS) {
			File polyZipFile = Files.getFileDownloaded(cfg, Files.GEO_NUTS_2016_60M_ZIP_LOCAL);
			File polyUnzipDir = Files.getFileCached(cfg, Files.GEO_NUTS_2016_60M_ZIP_DIR);
			if (!polyUnzipDir.exists()) {
				LOGGER.info("Extracting polygons from " + polyZipFile + " to " + polyUnzipDir);
				new ZipFile(polyZipFile).extractAll(polyUnzipDir.getAbsolutePath());
			}

			File polyGeoJson = new File(polyUnzipDir, Files.GEO_NUTS_2016_60M_GEOJSON);
			Map<String, PointsContainer> points = new PointsReader(polyGeoJson).extract().getResults();
			LOGGER.info("Extracted NUTS geo points, size: " + points.size());
		}

		// Build model

		if (STEP_4_RDF) {
			ModelBuilder modelBuilder = new ModelBuilder();
			modelBuilder.addNuts(nutsModel);
			modelBuilder.addLau(lau);
			LOGGER.info("Created model, size: " + modelBuilder.getModel().size());

			File outFile = Files.getFileCached(cfg, Files.FINAL_MODEL);
			FileOutputStream fos = new FileOutputStream(outFile);
			RDFDataMgr.write(fos, modelBuilder.getModel(), Lang.TURTLE);
			fos.close();
			LOGGER.info("Wrote file: " + outFile.getAbsolutePath());
		}
	}

	/**
	 * Loads configuration.
	 */
	private void initialize() {
		cfg = new Cfg();
		LOGGER.info("Internal cache: " + new File(cfg.get(CfgKeys.ioCacheDirectory)).getAbsolutePath());
		LOGGER.info("Downloads: " + new File(cfg.get(CfgKeys.ioDownloadDirectory)).getAbsolutePath());
	}

	/**
	 * Downloads files.
	 */
	private void download() {
		download(Files.NUTS_RDF, Files.NUTS_RDF_LOCAL);
		download(Files.LAU_2017_XLSX, Files.LAU_2017_XLSX_LOCAL);
		download(Files.GEO_NUTS_2016_60M_ZIP, Files.GEO_NUTS_2016_60M_ZIP_LOCAL);
		download(Files.GEO_LAU_2016_1M_ZIP, Files.GEO_LAU_2016_1M_ZIP_LOCAL);
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