/**
 *
 */
package gleem.linalg.open;

import gleem.linalg.Veci;

/**
 * @author Michael Kalkusch
 */
public final class Vec3iStartStop extends Vec3i {

	public static final int START_INDEX = 0;
	public static final int STOP_INDEX = 1;
	public static final int SOURCE_INDEX = 2;

	/**
	 *
	 */
	public Vec3iStartStop() {

		super();
	}

	/**
	 * @param source
	 */
	public Vec3iStartStop(Veci source) {

		super(source);
	}

	public final int getStartIndex() {
		return this.get(START_INDEX);
	}

	public final int getStopIndex() {
		return this.get(STOP_INDEX);
	}

	public final int getSourceId() {
		return this.get(STOP_INDEX);
	}

	public final void setStartStopSourceId(final int iStartIndex, final int iStopIndex,
			final int iSourceId) {
		this.setComponent(START_INDEX, iStartIndex);
		this.setComponent(STOP_INDEX, iStopIndex);
		this.setComponent(SOURCE_INDEX, iSourceId);
	}

	public final void setStartStop(final int iStartIndex, final int iStopIndex) {
		this.setComponent(START_INDEX, iStartIndex);
		this.setComponent(STOP_INDEX, iStopIndex);
	}

	public final void setStartStop(final Vec3iStartStop source) {
		this.setComponent(START_INDEX, source.getStartIndex());
		this.setComponent(STOP_INDEX, source.getStopIndex());
	}
}
