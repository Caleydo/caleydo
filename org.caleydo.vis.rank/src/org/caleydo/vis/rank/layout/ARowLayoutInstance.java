package org.caleydo.vis.rank.layout;


public abstract class ARowLayoutInstance implements IRowLayoutInstance {
	protected final int offset;
	protected final int numVisibles;
	protected final int selectedIndex;

	public ARowLayoutInstance(int offset, int numVisibles, int selectedIndex) {
		this.offset = offset;
		this.numVisibles = numVisibles;
		this.selectedIndex = selectedIndex;
	}

	@Override
	public boolean needsScrollBar() {
		return numVisibles < getSize();
	}

	/**
	 * @return the offset, see {@link #offset}
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the numVisibles, see {@link #numVisibles}
	 */
	@Override
	public int getNumVisibles() {
		return numVisibles;
	}
}