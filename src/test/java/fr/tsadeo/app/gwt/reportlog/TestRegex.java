package fr.tsadeo.app.gwt.reportlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestRegex {
	
	@Test
	public void testMatches() {
		
		final String source = "123qqqqqqqqqqqqqqq";
		boolean result = source.matches("^123.*");
		assertTrue(result);
		String newValue = source.replaceFirst("^123", "toto");
		System.out.println("newValue: " + newValue);
	}
	
	@Test
	public void testReplace() {
		
	    String source = "123AAA";

		String result = source.replaceFirst("^123", "toto");
		System.out.println("result: " + result);
		assertEquals("totoAAA", result);
		
		
		source = "BBBBBBBBB";
		result = source.replaceFirst("^123", "toto");
		System.out.println("result: " + result);
		assertEquals("BBBBBBBBB", result);
	}

}
