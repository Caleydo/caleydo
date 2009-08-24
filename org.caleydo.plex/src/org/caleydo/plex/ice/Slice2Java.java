package org.caleydo.plex.ice;

import java.io.File;

/**
 * Calls the Ice's slice2java program to generate java classes from the deskotheque related ice-files.
 * the program uses the environment variable ICE_HOME which must be set. 
 * @author Werner Puff
 *
 */
public class Slice2Java {
	public static void main(String[] args) {
		String iceHome = System.getenv("ICE_HOME");
		System.out.println("ICE_HOME = " + iceHome);
		if (iceHome == null || iceHome.isEmpty()) {
			System.out.println("ICE_HOME environment variable is not set");
			System.exit(1);
		}
		try {
			Runtime runtime = Runtime.getRuntime();
			String[] cmd = new String[4];
			cmd[0] = iceHome + File.separator + "bin" + File.separator + "slice2java";
			cmd[1] = "--output-dir";
			cmd[2] = "src";
			cmd[3] = "slice" + File.separator + "*.ice"; 
			runtime.exec(cmd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
