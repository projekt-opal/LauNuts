package org.dice_research.opal.launuts.polygons.parser;

import java.io.FileNotFoundException;
import org.junit.Assert; 
import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.parser.NutsParser;
import org.junit.Test;

public class NutParserHoleTest {
	
	public static TestMultiPolygons tester = new TestMultiPolygons();
	public static NutsParser nut_parser = new NutsParser();
	//nut_parser.createNutPolygons();
	
	//Pforzheim_Stadtkreis is a hole inside pforzheim_stadtkreis
	private static String nuts_code_of_Enzkreis = "DE12B";
	private static String nuts_code_of_Pforzheim_Stadtkreis = "DE129";
	
	//Eisenach_Kreisfreie_Stadt is a hole inside Wartburgkreis
	private static String nuts_code_of_Wartburgkreis = "DEG0P";
	private static String nuts_code_of_Eisenach_Kreisfreie_Stadt = "DEG0N";
	
	//Weimar Kreisfreie_Stadt is a hole inside Weimarer Land
	private static String nuts_code_of_Weimarer_Land = "DEG0G";
	private static String nuts_code_of_Weimar_Kreisfreie_Stadt = "DEG05";
	
	//Landau_in_der_Pfalz_Kreisfreie_Stadt is a hole inside Südliche_Weinstraße
	private static String nuts_code_of_Südliche_Weinstraße = "DEB3H";
	private static String nuts_code_of_Landau_in_der_Pfalz_Kreisfreie_Stadt = "DEB33";
	
	//Baden_baden is a hole inside Rastaat
	private static String nuts_code_of_Rastaat = "DEB3H";
	private static String nuts_code_of_Baden_Baden = "DEB33";
	
	//Berlin is a hole inside Brandenburg
	private static String nuts_code_of_Brandenburg = "DE40";
	private static String nuts_code_of_Berlin = "DE3";
	
	//Bremen is a hole inside Niedersachsen
	private static String nuts_code_of_Niedersachsen = "DE9";
	private static String nuts_code_of_Bremen = "DE501";
	
	//Ansbach is a hole inside Ansbach Landkreis
	private static String nuts_code_of_Ansbach_Landkreis = "DE256";
	private static String nuts_code_of_Ansbach = "DE251";
	
	//Ansbach is a hole inside Ansbach Landkreis
	private static String nuts_code_of_Coburg_Landkreis = "DE247";
	private static String nuts_code_of_Coburg_Kreisfreie_Stadt = "DE243";
	
	

	
	@Test
	public void testCase1ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Enzkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Pforzheim_Stadtkreis);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase2ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Wartburgkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Eisenach_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	}
	
	@Test
	public void testCase3ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Weimarer_Land, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Weimar_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase4ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Südliche_Weinstraße, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Landau_in_der_Pfalz_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase5ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Rastaat, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Baden_Baden);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase6ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Brandenburg, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Berlin);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase7ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Niedersachsen, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Bremen);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase8ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Ansbach_Landkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Ansbach);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase9ForHoleIntegrityCheck() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Coburg_Landkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Coburg_Kreisfreie_Stadt);
		Assert.assertEquals(true, tester.areTwoPolygonsEqual(a, b));
	
	}
}
