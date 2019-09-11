package org.dice_research.opal.launuts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static mappings.
 * 
 * @author Adrian Wilke
 */
public class Mapping {

	/**
	 * Returns map Nuts-URI to DBpedia-URI for NUTS-1.
	 */
	public Map<String, String> getNutsToDbPediaFederalStates() {
		// http://dbpedia.org/class/yago/WikicatStatesOfGermany
		Map<String, String> map = new HashMap<String, String>();

		// Not contained in ?place <http://dbpedia.org/ontology/country>
		// <http://dbpedia.org/resource/Germany>
		// map.put("http://data.europa.eu/nuts/code/DE",
		// "http://dbpedia.org/resource/Germany");

		map.put("http://data.europa.eu/nuts/code/DE1", "http://dbpedia.org/resource/Baden-Württemberg");
		map.put("http://data.europa.eu/nuts/code/DE2", "http://dbpedia.org/resource/Bavaria");
		map.put("http://data.europa.eu/nuts/code/DE3", "http://dbpedia.org/resource/Berlin");
		map.put("http://data.europa.eu/nuts/code/DE4", "http://dbpedia.org/resource/Brandenburg");
		map.put("http://data.europa.eu/nuts/code/DE5", "http://dbpedia.org/resource/Bremen_(state)");
		map.put("http://data.europa.eu/nuts/code/DE6", "http://dbpedia.org/resource/Hamburg");
		map.put("http://data.europa.eu/nuts/code/DE7", "http://dbpedia.org/resource/Hesse");
		map.put("http://data.europa.eu/nuts/code/DE8", "http://dbpedia.org/resource/Mecklenburg-Vorpommern");
		map.put("http://data.europa.eu/nuts/code/DE9", "http://dbpedia.org/resource/Lower_Saxony");
		map.put("http://data.europa.eu/nuts/code/DEA", "http://dbpedia.org/resource/North_Rhine-Westphalia");
		map.put("http://data.europa.eu/nuts/code/DEB", "http://dbpedia.org/resource/Rhineland-Palatinate");
		map.put("http://data.europa.eu/nuts/code/DEC", "http://dbpedia.org/resource/Saarland");
		map.put("http://data.europa.eu/nuts/code/DED", "http://dbpedia.org/resource/Saxony");
		map.put("http://data.europa.eu/nuts/code/DEE", "http://dbpedia.org/resource/Saxony-Anhalt");
		map.put("http://data.europa.eu/nuts/code/DEF", "http://dbpedia.org/resource/Schleswig-Holstein");
		map.put("http://data.europa.eu/nuts/code/DEG", "http://dbpedia.org/resource/Thuringia");
		return map;
	}

	/**
	 * Returns map Nuts-code to german label.
	 */
	public Map<String, String> getNutsCodeToPrefLabel() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("DE", "Deutschland");
		map.put("DE1", "Baden-Württemberg");
		map.put("DE2", "Bayern");
		map.put("DE3", "Berlin");
		map.put("DE4", "Brandenburg");
		map.put("DE5", "Bremen");
		map.put("DE6", "Hamburg");
		map.put("DE7", "Hessen");
		map.put("DE8", "Mecklenburg-Vorpommern");
		map.put("DE9", "Niedersachsen");
		map.put("DEA", "Nordrhein-Westfalen");
		map.put("DEB", "Rheinland-Pfalz");
		map.put("DEC", "Saarland");
		map.put("DED", "Sachsen");
		map.put("DEE", "Sachsen-Anhalt");
		map.put("DEF", "Schleswig-Holstein");
		map.put("DEG", "Thüringen");
		return map;
	}

	/**
	 * Returns unused Nuts-codes.
	 */
	public Set<String> getUnusedNutsCodes() {
		Set<String> set = new HashSet<String>();
		set.add("DEZ");
		set.add("DEZZ");
		set.add("DEZZZ");
		return set;
	}
}
