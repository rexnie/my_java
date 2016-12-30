package com.dc.misc;

import java.nio.charset.Charset;
import java.util.Set;

public class MiscTest {
	public static void main(String[] args) {
		MiscTest obj = new MiscTest();
		obj.testCharset();
		
	}
	
	void testCharset() {
		Charset cset = Charset.forName("ISO-8859-1");
		Set<String> aliases = cset.aliases();
		for (String alias: aliases)
			System.out.println(alias);
	}
	
}