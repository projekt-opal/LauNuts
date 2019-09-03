package org.dice_research.opal.launuts;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;

public class Main {

	public static void main(String[] args) throws IOException {
//		LauCsvParser csvParser = new LauCsvParser();
//		csvParser.parse("/home/adrian/DATA/NUTS-LAU/EU-28-LAU-2019-NUTS-2016-DE.csv");

		NutsRdf nutsRdf = new NutsRdf();
		nutsRdf.loadModel("/home/adrian/DATA/NUTS-LAU/nuts.rdf");

		Map<Resource, String> x = nutsRdf.getNarrower(NutsRdf.RES_DE);
		for (Entry<Resource, String> y : x.entrySet()) {
			System.out.println(y.getKey() + " " + y.getValue());

			Map<Resource, String> x2 = nutsRdf.getNarrower(y.getKey());
			for (Entry<Resource, String> y2 : x2.entrySet()) {
				System.out.println(y2.getKey() + " " + y2.getValue());

				Map<Resource, String> x3 = nutsRdf.getNarrower(y2.getKey());
				for (Entry<Resource, String> y3 : x3.entrySet()) {
					System.out.println(y3.getKey() + " " + y3.getValue());
				}

			}
		}
	}
}