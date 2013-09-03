package org.caleydo.data.tcga.internal.model;

import java.util.ArrayList;
import java.util.List;

public class AdditionalPerspectiveInfo {
	private int count;
	private List<String> groupings = new ArrayList<>();

	/**
	 * @return the count, see {@link #count}
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count setter, see {@link count}
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param groupings
	 *            setter, see {@link groupings}
	 */
	public void setGroupings(List<String> groupings) {
		this.groupings = groupings;
	}

	/**
	 * @return the groupings, see {@link #groupings}
	 */
	public List<String> getGroupings() {
		return groupings;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdditionalPerspectiveInfo [count=");
		builder.append(count);
		builder.append(", groupings=");
		builder.append(groupings);
		builder.append("]");
		return builder.toString();
	}

}