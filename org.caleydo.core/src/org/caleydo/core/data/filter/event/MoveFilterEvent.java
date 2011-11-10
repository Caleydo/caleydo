package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;

/**
 * @author Thomas Geymayer
 */
public class MoveFilterEvent<FilterType extends Filter<?>>
	extends FilterEvent<FilterType> {

	private int offset = 0;

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public boolean checkIntegrity() {
		if (!super.checkIntegrity() || offset == 0)
			return false;

		return true;
	}
}
