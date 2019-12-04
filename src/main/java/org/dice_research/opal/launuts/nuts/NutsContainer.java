package org.dice_research.opal.launuts.nuts;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.dice_research.opal.launuts.Vocabularies;

/**
 * Container for NUTS data. Used in {@link NutsRdfExtractor}.
 * 
 * @author Adrian Wilke
 */
public class NutsContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String uriToNutsCode(String uri) {
		if (uri.startsWith(Vocabularies.NS_EU_NUTS_CODE)) {
			return uri.substring(Vocabularies.NS_EU_NUTS_CODE.length());
		} else {
			throw new RuntimeException("Wrong URI: " + uri);
		}
	}

	public Integer nutsLevel;

	public Set<String> prefLabel = new HashSet<String>();
	public String notation;
	public NutsContainer parent;

	public String replacedBy;
	public Set<String> replaces = new HashSet<String>();

	public String mergedInto;
	public Set<String> mergedFrom = new HashSet<String>();

	public String getUri() {
		return Vocabularies.NS_EU_NUTS_CODE + notation;
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

	public static String toSimpleName(String label) {
		if (label.contains(",")) {
			label = label.substring(0, label.indexOf(","));
		}
		if (label.contains("(")) {
			label = label.substring(0, label.indexOf("("));
		}
		return label.trim();
	}
}