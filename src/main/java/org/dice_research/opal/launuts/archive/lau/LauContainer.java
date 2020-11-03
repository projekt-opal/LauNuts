package org.dice_research.opal.launuts.archive.lau;

import java.io.Serializable;

import org.dice_research.opal.launuts.archive.Vocabularies;

/**
 * Container for LAU data. Used in {@link LauCsvParser}.
 * 
 * @author Adrian Wilke
 */
public class LauContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String lauCode;
	public String lauNameLatin;
	public String lauNameNational;
	public String nuts3code;

	public String getSimpleName() {
		String simpleName = lauNameNational;
		String[] parts = lauNameNational.split(",");
		if (parts.length > 1) {
			simpleName = parts[0];
		}
		return simpleName;
	}

	public String getUri() {
		return Vocabularies.NS_LAU_DE + lauCode;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getSimpleName());
		stringBuilder.append(" | ");
		stringBuilder.append(lauNameLatin);
		stringBuilder.append(" | ");
		stringBuilder.append(lauCode);
		return stringBuilder.toString();
	}
}