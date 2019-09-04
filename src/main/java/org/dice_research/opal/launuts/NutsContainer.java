package org.dice_research.opal.launuts;

import java.util.HashSet;
import java.util.Set;

/**
 * Container for NUTS data.
 * 
 * @author Adrian Wilke
 */
public class NutsContainer {

	public static String uriToNutsCode(String uri) {
		if (uri.startsWith(Vocabularies.NS_NUTS_CODE)) {
			return uri.substring(Vocabularies.NS_NUTS_CODE.length());
		} else {
			throw new RuntimeException("Wrong URI: " + uri);
		}
	}

	public String prefLabel;
	public String notation;

	public String replacedBy;
	public Set<String> replaces = new HashSet<String>();

	public String mergedInto;
	public Set<String> mergedFrom = new HashSet<String>();

	public String getUri() {
		return Vocabularies.NS_NUTS_CODE + notation;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(prefLabel);
		stringBuilder.append(" | ");
		stringBuilder.append(notation);
		stringBuilder.append(" | ");
		stringBuilder.append(getUri());
		return stringBuilder.toString();
	}
}