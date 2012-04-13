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
package org.caleydo.testing.collection;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

public class VirtualArrayTester extends TestCase {

	RecordVirtualArray virtualArray;
	int size = 25000;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		virtualArray = new RecordVirtualArray(IDType.registerType("Test",
				IDCategory.registerCategory("Test"), EColumnType.FLOAT));
		for (int count = 0; count < size; count++)
			virtualArray.append(count);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBasics() {
		assertEquals(size, virtualArray.size());
	}

	public void testBulkRemoveIndex() {
		ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
		{
			for (int count = 0; count < size; count++)
				shuffeledList.add(count);
		}
		Collections.shuffle(shuffeledList);
		virtualArray.removeInBulk(shuffeledList);

		assertEquals(0, virtualArray.size());
	}

	// public void testRemoveID() {
	// ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
	// {
	// for (int count = 0; count < size; count++)
	// shuffeledList.add(count);
	// }
	// Collections.shuffle(shuffeledList);
	// for (Integer id : shuffeledList)
	// virtualArray.removeByElement(id);
	//
	// assertEquals(0, virtualArray.size().intValue());
	// }

	public void testRemoveIDWithDelta() {

		ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
		{
			for (int count = 0; count < size; count++)
				shuffeledList.add(count);
		}
		Collections.shuffle(shuffeledList);

		remove(0, 501, shuffeledList);
		remove(500, size, shuffeledList);

		assertEquals(0, virtualArray.size());
	}

	private void remove(int from, int to, ArrayList<Integer> shuffeledList) {

		RecordVADelta delta = new RecordVADelta();

		for (int count = from; count < to; count++) {
			Integer id = shuffeledList.get(count);
			delta.add(VADeltaItem.removeElement(id));
		}

		virtualArray.setDelta(delta);
	}

}
