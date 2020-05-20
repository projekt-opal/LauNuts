package org.dice_research.opal.launuts.polygons;

import java.io.File;
import java.util.List;

import org.dice_research.opal.launuts.lau.LauReaderInterface;

/**
 * Interface for polygon parser.
 * 
 * TODO This is a development recommendation, changes are allowed.
 * 
 * @see NUTS (NUTS 2016, geoJSON, all 5 scales)
 *      https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units/nuts
 * @see LAU (LAU 2018, Shapefile)
 *      https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units/lau
 * @see Licensing and copyright
 *      https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units
 * 
 * @author Adrian Wilke
 */
public interface PolygonParserInterface {

	/**
	 * Returns center points of multi-polygons for a related LAU code.
	 */
	public List<Point> getLauCenterPoints(String lauCode) throws PolygonParserException;

	/**
	 * Returns a multi-polygon for a related LAU code.
	 */
	public MultiPolygon getLauPolygon(String lauCode) throws PolygonParserException;

	/**
	 * Returns center points of multi-polygons for a related NUTS code.
	 */
	public List<Point> getNutsCenterPoints(String nutsCode) throws PolygonParserException;

	/**
	 * Returns a multi-polygon for a related NUTS code.
	 */
	public MultiPolygon getNutsPolygon(String nutsCode) throws PolygonParserException;

	/**
	 * Sets source directory for polygon files. Should contain Shapefile and/or
	 * geoJSON format.
	 */
	public LauReaderInterface setSourceDirectory(File directory) throws PolygonParserException;
}