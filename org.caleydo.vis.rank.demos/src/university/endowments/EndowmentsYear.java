/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.endowments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * http://chronicle.com/article/CollegeUniversity/136933/
 *
 * @author Samuel Gratzl
 *
 */
public class EndowmentsYear {
	public static Map<String, int[]> readData() throws IOException, ParseException {
		Map<String, int[]> data = new LinkedHashMap<>();
		NumberFormat f = NumberFormat.getIntegerInstance(Locale.ENGLISH);
		f.setParseIntegerOnly(true);
		f.setGroupingUsed(true);
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				EndowmentsYear.class.getResourceAsStream("data.txt"), Charset.forName("UTF-8")))) {
			String line;
			while ((line = r.readLine()) != null) {
				String[] l = line.split(";");
				String school = l[1];
				System.out.println(Arrays.toString(l));
				int endowment2012 = f.parse(l[3]).intValue();
				int endowment2011 = l.length >= 5 ? f.parse(l[4]).intValue() : 0;
				data.put(school, new int[] { endowment2011, endowment2012 });
			}
		}
		return data;
	}
}
