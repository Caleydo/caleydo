package org.caleydo.core.math.statistics.histogram;


/**
 * Define type of statistic histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public enum StatisticHistogramType
{

	REGULAR_LINEAR("Regular spacing, linear distribution"),
	
	REGULAR_LOG("Regular spacing in log distribution"),
	
	REGULAR_LOG_INV("Regular spacing in log distribution"),
	
	NON_REGULAR("Non-regular spacing using a lookup table, with strict monoton assending values");
	
	/**
	 * Remark describing window toolkit.
	 */
	private final String sRemark;
	
	/**
	 * Constructor.
	 * 
	 * @param setRemark details on toolkit and version of toolkit.
	 */
	private StatisticHistogramType(String setRemark) {
		this.sRemark = setRemark;
	}
	
	/**
	 * Details on toolkit and required version of toolkit.
	 * 
	 * @return toolkit description adn version.
	 */
	public String getRemark() {
		return this.sRemark;
	}
	
	/**
	 * Get StatisticHistogramType by index.
	 * 
	 * 0 ....REGULAR_LINEAR
	 * 1 ....REGULAR_LOG
	 * 2 ....REGULAR_LOG_INV
	 * 
	 * 10....NON_REGULAR
	 * 
	 * 
	 * @param iIndex valid input [0..2],[10]
	 * @return StatisticHistogramType
	 */
	public static final StatisticHistogramType getTypeByIndex( final int iIndex ) {
		switch (iIndex) {
		case 0:
			return StatisticHistogramType.REGULAR_LINEAR;
			
		case 1:
			return StatisticHistogramType.REGULAR_LOG;
			
		case 2:
			return StatisticHistogramType.REGULAR_LOG_INV;
			
		case 10:
			return StatisticHistogramType.NON_REGULAR;
			
			default:
				assert false : "index is not supported!";
				return StatisticHistogramType.REGULAR_LINEAR;
		}
	}
	
	/**
	 * Increment type.
	 * Possible outpus: REGULAR_LINEAR, REGULAR_LOG or REGULAR_LOG_INV
	 * 
	 * @param current 
	 * @return current type incremented (may be REGULAR_LINEAR, REGULAR_LOG or REGULAR_LOG_INV)
	 */
	public StatisticHistogramType incrementMode() {
		int iOrdinal = this.ordinal();
		
		if ( iOrdinal >= 2 ) 
		{
			return StatisticHistogramType.REGULAR_LINEAR;
		}
		
		return getTypeByIndex( iOrdinal + 1 );
	}
}
