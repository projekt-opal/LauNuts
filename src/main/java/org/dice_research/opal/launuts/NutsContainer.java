package org.dice_research.opal.launuts;

/**
 * Container for NUTS data.
 * 
 * @author Adrian Wilke
 */
public class NutsContainer {

	public String key;
	public String prefLabel;
	public String notation;

	public String getUri() {
		return Vocabularies.NS_NUTS + key;
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