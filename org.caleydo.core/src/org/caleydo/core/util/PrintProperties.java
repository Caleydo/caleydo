package org.caleydo.core.util;

import java.util.Enumeration;
import java.util.Properties;

public class PrintProperties {
	public static void main(String[] args) {
		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) p.get(key);
			System.out.println(key + ": " + value);
		}
	}
}
