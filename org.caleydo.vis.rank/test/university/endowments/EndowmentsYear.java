/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
