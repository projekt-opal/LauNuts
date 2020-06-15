package org.dice_research.opal.launuts.polygons.parser;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.parser.NutsParser;
import org.junit.Test;

/**
 * In this test class, we will perform two tests.
 * 
 * The first test is to confirm the integrity of holes inside nuts in
 * NUTS_Polygons.json file.
 * 
 * The second test is to confirm the validity of a few important methods which
 * were used in NutsParser class to extract polygons from GeoJson files provides
 * from Eurostat.
 * 
 * @author Gourab Sahu
 *
 */

public class NutParserTest {

	public TestMultiPolygons tester = new TestMultiPolygons();
	public NutsParser nutParser = new NutsParser();
	MultiPolygon hole;

	// Pforzheim_Stadtkreis is a hole inside pforzheim_stadtkreis
	static final String NUTS_CODE_OF_ENZKREIS = "DE12B";
	static final String NUTS_CODE_OF_PFORZHEIM_STADTKREIS = "DE129";

	// Eisenach_Kreisfreie_Stadt is a hole inside Wartburgkreis
	static final String NUTS_CODE_OF_WARTBURGKREIS = "DEG0P";
	static final String NUTS_CODE_OF_EISENACH_KREISFREIE_STADT = "DEG0N";

	// Weimar Kreisfreie_Stadt is a hole inside Weimarer Land
	static final String NUTS_CODE_OF_WEIMARER_LAND = "DEG0G";
	static final String NUTS_CODE_OF_WEIMAR_KREISFREIE_STADT = "DEG05";

	// Landau_in_der_Pfalz_Kreisfreie_Stadt is a hole inside Südliche_Weinstraße
	static final String NUTS_CODE_OF_SÜDLICHE_WEINSTRASSE = "DEB3H";
	static final String NUTS_CODE_OF_LANDAU_IN_DER_PFALZ_KREISFREIE_STADT = "DEB33";

	// Baden_baden is a hole inside Rastaat
	static final String NUTS_CODE_OF_RASTAAT = "DEB3H";
	static final String NUTS_CODE_OF_BADEN_BADEN = "DEB33";

	// Berlin is a hole inside Brandenburg
	static final String NUTS_CODE_OF_BRANDENBURG = "DE40";
	static final String NUTS_CODE_OF_BERLIN = "DE3";

	// Bremen is a hole inside Niedersachsen
	static final String NUTS_CODE_OF_NIEDERSACHSEN = "DE9";
	static final String NUTS_CODE_OF_BREMEN = "DE501";

	// Ansbach is a hole inside Ansbach Landkreis
	static final String NUTS_CODE_OF_ANSBACH_LANDKREIS = "DE256";
	static final String NUTS_CODE_OF_ANSBACH = "DE251";

	// Ansbach is a hole inside Ansbach Landkreis
	static final String NUTS_CODE_OF_COBURG_LANDKREIS = "DE247";
	static final String NUTS_CODE_OF_COBURG_KREISFREIE_STADT = "DE243";
	
	// Paderborn is not a hole inside München
	static final String NUTS_CODE_OF_PADERBORN = "DEA47";
	static final String NUTS_CODE_OF_MÜNCHEN = "DE212";
	
	// Koblennz is not a hole inside Leipzig
	static final String NUTS_CODE_OF_KOBLENZ = "DEB1";
	static final String NUTS_CODE_OF_LEIPZIG = "DED5";
	
	// Heidelberg is not a hole inside dresden
	static final String NUTS_CODE_OF_HEIDELBERG = "DE125";
	static final String NUTS_CODE_OF_DRESDEN = "DED21";
	
	//

	@Before
	public void setUp() throws Exception {
		hole = new MultiPolygon();
	}

	@After
	public void tearDown() {
		hole = null;
	}

	@Test
	public void testCaseForHoleTestingOfEnzkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_ENZKREIS, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_PFORZHEIM_STADTKREIS);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfWartburgkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_WARTBURGKREIS, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_EISENACH_KREISFREIE_STADT);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));
	}

	@Test
	public void testCaseForHoleTestingOfWeimarLand()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_WEIMARER_LAND, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_WEIMAR_KREISFREIE_STADT);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfSüdlicheweinstraße()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_SÜDLICHE_WEINSTRASSE, 1, 1));
		MultiPolygon coorespondingNuts = nutParser
				.getNutsPolygon(NUTS_CODE_OF_LANDAU_IN_DER_PFALZ_KREISFREIE_STADT);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfBadenBaden()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_RASTAAT, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_BADEN_BADEN);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfBrandenburg()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_BRANDENBURG, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_BERLIN);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfNiedersachsen()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_NIEDERSACHSEN, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_BREMEN);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfAnsbachlandkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_ANSBACH_LANDKREIS, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_ANSBACH);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

	@Test
	public void testCaseForHoleTestingOfCoburglandkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_COBURG_LANDKREIS, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_COBURG_KREISFREIE_STADT);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}
	
	@Test
	public void testCaseFalseHoleTestingOfMunich()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_PADERBORN, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_MÜNCHEN);
		Assert.assertEquals(false, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}
	
	@Test
	public void testCaseFalseHoleTestingOfLeipzig()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_KOBLENZ, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_LEIPZIG);
		Assert.assertEquals(false, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}
	
	@Test
	public void testCaseFalseHoleTestingOfDresden()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nutParser.getHole(NUTS_CODE_OF_HEIDELBERG, 1, 1));
		MultiPolygon coorespondingNuts = nutParser.getNutsPolygon(NUTS_CODE_OF_DRESDEN);
		Assert.assertEquals(false, tester.areTwoMultiPolygonsEqual(hole, coorespondingNuts));

	}

}
