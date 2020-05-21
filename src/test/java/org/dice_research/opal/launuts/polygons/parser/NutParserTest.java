package org.dice_research.opal.launuts.polygons.parser;

import java.io.FileNotFoundException;

import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.parser.NutParser;

public class NutParserTest {

	public static void main(String[] args) throws PolygonParserException, ClassCastException, FileNotFoundException {
		NutParser nut_parser = new NutParser();

		nut_parser.createNutPolygons();

		// System.out.println(nut_parser.getNutsPolygon("DEF04").toString());
	}
}
