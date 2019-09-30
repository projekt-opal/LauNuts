package org.dice_research.opal.launuts;

import org.dice_research.opal.launuts.matcher.MatcherVersion1;
import org.dice_research.opal.launuts.matcher.MatcherVersion2;

/**
 * Runs matcher.
 * 
 * @author Adrian Wilke
 */
public class MatcherTest {

	public static void main(String[] args) throws Exception {
		
		new MatcherVersion1().run();

		System.out.println();
		System.out.println("---");
		System.out.println();
		
		new MatcherVersion2().run();
	}

}