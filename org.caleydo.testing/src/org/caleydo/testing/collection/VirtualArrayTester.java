package org.caleydo.testing.collection;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

public class VirtualArrayTester extends TestCase {

	RecordVirtualArray virtualArray;
	int size = 25000;

	protected void setUp() throws Exception {
		super.setUp();
		virtualArray = new RecordVirtualArray();
		for (int count = 0; count < size; count++)
			virtualArray.append(count);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBasics() {
		assertEquals(size, virtualArray.size().intValue());
	}

	public void testBulkRemoveIndex() {
		ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
		{
			for (int count = 0; count < size; count++)
				shuffeledList.add(count);
		}
		Collections.shuffle(shuffeledList);
		virtualArray.removeInBulk(shuffeledList);

		assertEquals(0, virtualArray.size().intValue());
	}

//	public void testRemoveID() {
//		ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
//		{
//			for (int count = 0; count < size; count++)
//				shuffeledList.add(count);
//		}
//		Collections.shuffle(shuffeledList);
//		for (Integer id : shuffeledList)
//			virtualArray.removeByElement(id);
//
//		assertEquals(0, virtualArray.size().intValue());
//	}

	public void testRemoveIDWithDelta() {

		ArrayList<Integer> shuffeledList = new ArrayList<Integer>();
		{
			for (int count = 0; count < size; count++)
				shuffeledList.add(count);
		}
		Collections.shuffle(shuffeledList);

		remove(0, 501, shuffeledList);
		remove(500, size, shuffeledList);

		assertEquals(0, virtualArray.size().intValue());
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
