package org.caleydo.core.event.data;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 * @author Marc Streit
 */
public class StatisticsFoldChangeReductionEvent
	extends AEvent {

	private DataContainer dataContainer1;
	private DataContainer dataContainer2;
	/**
	 * A fold change can be calculated between the records or the dimensions of the two specified containers.
	 * If this flag is set to be true, it is calculated for the records, els for the dimensions
	 */
	private boolean betweenRecords;

	/**
	 * @param dataContainer1
	 *            set to {@link #dataContainer1}
	 * @param dataContainer2
	 *            set to {@link #dataContainer2}
	 * @param betweenRecords
	 *            set to {@link #betweenRecords}
	 */
	public StatisticsFoldChangeReductionEvent(DataContainer dataContainer1, DataContainer dataContainer2,
		boolean betweenRecords) {
		this.dataContainer1 = dataContainer1;
		this.dataContainer2 = dataContainer2;
		this.betweenRecords = betweenRecords;
	}

	/**
	 * @return the dataContainer1, see {@link #dataContainer1}
	 */
	public DataContainer getDataContainer1() {
		return dataContainer1;
	}

	/**
	 * @param dataContainer1
	 *            setter, see {@link #dataContainer1}
	 */
	public void setDataContainer1(DataContainer dataContainer1) {
		this.dataContainer1 = dataContainer1;
	}

	/**
	 * @return the dataContainer2, see {@link #dataContainer2}
	 */
	public DataContainer getDataContainer2() {
		return dataContainer2;
	}

	/**
	 * @param dataContainer2
	 *            setter, see {@link #dataContainer2}
	 */
	public void setDataContainer2(DataContainer dataContainer2) {
		this.dataContainer2 = dataContainer2;
	}

	/**
	 * @return the betweenRecords, see {@link #betweenRecords}
	 */
	public boolean isBetweenRecords() {
		return betweenRecords;
	}

	/**
	 * @param betweenRecords
	 *            setter, see {@link #betweenRecords}
	 */
	public void setBetweenRecords(boolean betweenRecords) {
		this.betweenRecords = betweenRecords;
	}

	@Override
	public boolean checkIntegrity() {

		if (dataContainer1 == null || dataContainer2 == null)
			return false;

		return true;
	}
}
