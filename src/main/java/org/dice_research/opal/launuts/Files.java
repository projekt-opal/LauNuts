package org.dice_research.opal.launuts;

import java.io.File;

/**
 * Files.
 * 
 * @see NUTS: https://ec.europa.eu/eurostat/web/nuts/background
 * @see NUTS RDF:
 *      https://data.europa.eu/euodp/en/data/dataset/ESTAT-NUTS-classification
 * @see LAU XLSX:
 *      https://ec.europa.eu/eurostat/web/nuts/local-administrative-units
 * @see geodata:
 *      https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units
 *
 * @author Adrian Wilke
 */
public abstract class Files {

	public static final String CACHE_LAU = "lau.cache";
	public static final String CACHE_LAU_GEO = "lau-geo.cache";
	
	public static final String FINAL_MODEL = "LauNuts-" + Main.VERSION + ".ttl";
	public static final String FINAL_OPAL = "places-de-at.txt";

	public static final String NUTS_RDF = "https://data.europa.eu/euodp/repository/ec/estat/nuts/nuts.rdf";
	public static final String NUTS_RDF_LOCAL = "nuts.rdf";

//  Was too large to process in Java
//	public static final String LAU_2019_XLSX = "https://ec.europa.eu/eurostat/documents/345175/501971/EU-28-LAU-2019-NUTS-2016.xlsx";
//	public static final String LAU_2019_XLSX_LOCAL = "EU-28-LAU-2019-NUTS-2016.xlsx";

	public static final String LAU_2017_XLSX = "https://ec.europa.eu/eurostat/documents/345175/501971/EU-28_LAU_2017_NUTS_2016.xlsx";
	public static final String LAU_2017_XLSX_LOCAL = "EU-28_LAU_2017_NUTS_2016.xlsx";

	public static final String GEO_NUTS_2016_60M_ZIP = "http://gisco-services.ec.europa.eu/distribution/v2/nuts/download/ref-nuts-2021-60m.geojson.zip";
	public static final String GEO_NUTS_2016_60M_ZIP_LOCAL = "ref-nuts-2021-60m.geojson.zip";
	public static final String GEO_NUTS_2016_60M_ZIP_DIR = "ref-nuts-2021-60m";
	public static final String GEO_NUTS_2016_60M_GEOJSON = "NUTS_LB_2021_4326.geojson";

	public static final String GEO_LAU_2016_1M_ZIP = "http://gisco-services.ec.europa.eu/distribution/v2/lau/download/ref-lau-2016-01m.geojson.zip";
	public static final String GEO_LAU_2016_1M_ZIP_LOCAL = "ref-lau-2016-01m.geojson.zip";
	public static final String GEO_LAU_2016_1M_ZIP_DIR = "ref-lau-2016-01m";
	public static final String GEO_LAU_2016_1M_GEOJSON = "LAU_RG_01M_2016_4326.geojson";

	public static File getFileDownloaded(Cfg cfg, String filename) {
		return new File(cfg.get(CfgKeys.ioDownloadDirectory), filename);
	}

	public static File getFileCached(Cfg cfg, String filename) {
		return new File(cfg.get(CfgKeys.ioCacheDirectory), filename);
	}
}