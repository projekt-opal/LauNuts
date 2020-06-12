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
	public NutsParser nut_parser = new NutsParser();
	MultiPolygon hole;

	// Pforzheim_Stadtkreis is a hole inside pforzheim_stadtkreis
	private static String nuts_code_of_Enzkreis = "DE12B";
	private static String nuts_code_of_Pforzheim_Stadtkreis = "DE129";

	// Eisenach_Kreisfreie_Stadt is a hole inside Wartburgkreis
	private static String nuts_code_of_Wartburgkreis = "DEG0P";
	private static String nuts_code_of_Eisenach_Kreisfreie_Stadt = "DEG0N";

	// Weimar Kreisfreie_Stadt is a hole inside Weimarer Land
	private static String nuts_code_of_Weimarer_Land = "DEG0G";
	private static String nuts_code_of_Weimar_Kreisfreie_Stadt = "DEG05";

	// Landau_in_der_Pfalz_Kreisfreie_Stadt is a hole inside Südliche_Weinstraße
	private static String nuts_code_of_Südliche_Weinstraße = "DEB3H";
	private static String nuts_code_of_Landau_in_der_Pfalz_Kreisfreie_Stadt = "DEB33";

	// Baden_baden is a hole inside Rastaat
	private static String nuts_code_of_Rastaat = "DEB3H";
	private static String nuts_code_of_Baden_Baden = "DEB33";

	// Berlin is a hole inside Brandenburg
	private static String nuts_code_of_Brandenburg = "DE40";
	private static String nuts_code_of_Berlin = "DE3";

	// Bremen is a hole inside Niedersachsen
	private static String nuts_code_of_Niedersachsen = "DE9";
	private static String nuts_code_of_Bremen = "DE501";

	// Ansbach is a hole inside Ansbach Landkreis
	private static String nuts_code_of_Ansbach_Landkreis = "DE256";
	private static String nuts_code_of_Ansbach = "DE251";

	// Ansbach is a hole inside Ansbach Landkreis
	private static String nuts_code_of_Coburg_Landkreis = "DE247";
	private static String nuts_code_of_Coburg_Kreisfreie_Stadt = "DE243";

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

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Enzkreis, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Pforzheim_Stadtkreis);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfWartburgkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Wartburgkreis, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Eisenach_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));
	}

	@Test
	public void testCaseForHoleTestingOfWeimarLand()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Weimarer_Land, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Weimar_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfSüdlicheweinstraße()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Südliche_Weinstraße, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser
				.getNutsPolygon(nuts_code_of_Landau_in_der_Pfalz_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfBadenBaden()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Rastaat, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Baden_Baden);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfBrandenburg()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Brandenburg, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Berlin);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfNiedersachsen()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Niedersachsen, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Bremen);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfAnsbachlandkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Ansbach_Landkreis, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Ansbach);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

	@Test
	public void testCaseForHoleTestingOfCoburglandkreis()
			throws PolygonParserException, ClassCastException, FileNotFoundException {

		hole.polygons.add(nut_parser.getHole(nuts_code_of_Coburg_Landkreis, 1, 1));
		MultiPolygon cooresponding_nuts_of_hole = nut_parser.getNutsPolygon(nuts_code_of_Coburg_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(hole, cooresponding_nuts_of_hole));

	}

}
