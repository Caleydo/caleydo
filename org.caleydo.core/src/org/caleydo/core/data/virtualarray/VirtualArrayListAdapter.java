/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.collect.Lists;

/**
 * custom conversion of a List of Integer to an XML String with compression
 * 
 * assumption: just positive indices with in most of the cases increasing order
 * 
 * notation:
 * 
 * <pre>
 * number[-count]
 * </pre>
 *
 * example
 * 
 * <pre>
 * from: [1, 2, 3, 4, 5, 8, 9, 12, 14, 16, 17, 18, 19, 20, 21, 30]
 * to: [1 -4 8 -1 12 14 16 -5 30]
 * </pre>
 * 
 * @author Samuel Gratzl
 * 
 */
public class VirtualArrayListAdapter extends XmlAdapter<String,ArrayList<Integer>> {

	@Override
	public ArrayList<Integer> unmarshal(String v) throws Exception {
		try (Scanner s = new Scanner(v)) {
			ArrayList<Integer> r = new ArrayList<>();
			int last = 0;
			while (s.hasNextInt()) {
				int act = s.nextInt();
				if (act < 0) { // count
					for (int i = 0; i < -act; ++i) {
						r.add(++last);
					}
				} else {
					last = act;
					r.add(act);
				}
			}
			return r;
		}
	}

	@Override
	public String marshal(ArrayList<Integer> v) throws Exception {
		StringBuilder b = new StringBuilder();
		int expected = -1;
		int count = 0;
		for (int vi : v) {
			if (vi == expected) {
				expected++;
				count++;
			} else {
				if (count > 0)
					b.append(-count).append(' ');
				b.append(vi).append(' ');
				expected = vi + 1;
				count = 0;
			}
		}
		if (count > 0)
			b.append(-count).append(' ');
		return b.toString();
	}

	public static void main(String[] args) throws Exception {
		final VirtualArrayListAdapter adapter = new VirtualArrayListAdapter();
		ArrayList<Integer> a = Lists.newArrayList(1, 2, 3, 4, 5, 8, 9, 12, 14, 16, 17, 18, 19, 20, 21, 30);
		System.out.println(a);
		String s = adapter.marshal(a);
		System.out.println(s);
		ArrayList<Integer> b = adapter.unmarshal(s);
		System.out.println(b);
	}
}
