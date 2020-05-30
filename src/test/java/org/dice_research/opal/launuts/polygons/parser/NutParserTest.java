package org.dice_research.opal.launuts.polygons.parser;

import java.io.FileNotFoundException;
import java.io.Reader;
import org.junit.Assert; 
import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.parser.NutsParser;
import org.junit.Test;

public class NutParserTest {
	
	TestMultiPolygons tester = new TestMultiPolygons();
	NutsParser nut_parser = new NutsParser();
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

	
	@Test
	public void testCase1() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Enzkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Pforzheim_Stadtkreis);
		TestMultiPolygons tester = new TestMultiPolygons();
		Assert.assertEquals(true, tester.AreTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase2() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Wartburgkreis, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Eisenach_Kreisfreie_Stadt);
		TestMultiPolygons tester = new TestMultiPolygons();
		Assert.assertEquals(true, tester.AreTwoPolygonsEqual(a, b));
	}
	
	@Test
	public void testCase3() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Weimarer_Land, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Weimar_Kreisfreie_Stadt);
		TestMultiPolygons tester = new TestMultiPolygons();
		Assert.assertEquals(true, tester.AreTwoPolygonsEqual(a, b));
	
	}
	
	@Test
	public void testCase4() throws PolygonParserException, ClassCastException, FileNotFoundException{
		
		MultiPolygon a = nut_parser.getMultiPolygonFromHole(nuts_code_of_Südliche_Weinstraße, 1);
		MultiPolygon b = nut_parser.getNutsPolygon(nuts_code_of_Landau_in_der_Pfalz_Kreisfreie_Stadt);
		TestMultiPolygons tester = new TestMultiPolygons();
		Assert.assertEquals(true, tester.AreTwoPolygonsEqual(a, b));
	
	}
}
