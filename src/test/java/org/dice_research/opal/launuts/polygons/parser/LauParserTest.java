package org.dice_research.opal.launuts.polygons.parser;

import java.io.IOException;

public class LauParserTest {
	
	public static void main(String[] args) throws IOException, Exception {
		
		LauParser lau_parser = new LauParser();
		lau_parser.createLauPolygons();
		//System.out.println(lau_parser.getLauPolygon("DE_01004000"));
	}

}
