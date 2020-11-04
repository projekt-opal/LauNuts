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
	public static final String FINAL_MODEL = "LauNuts-" + Main.VERSION + ".ttl";

	public static final String NUTS_RDF = "https://data.europa.eu/euodp/repository/ec/estat/nuts/nuts.rdf";
	public static final String NUTS_RDF_LOCAL = "nuts.rdf";

	public static final String LAU_2019_XLSX = "https://ec.europa.eu/eurostat/documents/345175/501971/EU-28-LAU-2019-NUTS-2016.xlsx";
	public static final String LAU_2019_XLSX_LOCAL = "EU-28-LAU-2019-NUTS-2016.xlsx";

	public static final String LAU_2017_XLSX = "https://ec.europa.eu/eurostat/documents/345175/501971/EU-28_LAU_2017_NUTS_2016.xlsx";
	public static final String LAU_2017_XLSX_LOCAL = "EU-28_LAU_2017_NUTS_2016.xlsx";

	public static File getFileDownloaded(Cfg cfg, String filename) {
		return new File(cfg.get(CfgKeys.ioDownloadDirectory), filename);
	}

	public static File getFileCached(Cfg cfg, String filename) {
		return new File(cfg.get(CfgKeys.ioCacheDirectory), filename);
	}
}