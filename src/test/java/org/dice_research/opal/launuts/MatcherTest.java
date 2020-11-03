package org.dice_research.opal.launuts;

import org.dice_research.opal.launuts.archive.matcher.MatcherVersion1;
import org.dice_research.opal.launuts.archive.matcher.MatcherVersion2;

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

		MatcherVersion2 matcher = new MatcherVersion2();
		matcher.timeoutAfterLoadingData = 50;
		matcher.run();
	}

}