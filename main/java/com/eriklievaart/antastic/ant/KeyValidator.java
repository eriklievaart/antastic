package com.eriklievaart.antastic.ant;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class KeyValidator {
	private static final String PROPERTY_PATTERN = "[-._0-9a-z]++";

	public static void check(String key) {
		Check.matches(key, PROPERTY_PATTERN, "invalid key % should match $", key, PROPERTY_PATTERN);
	}

}
