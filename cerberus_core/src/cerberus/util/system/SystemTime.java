package cerberus.util.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * returns the system-time.
 * Server source code.
 *
 * @author Michael Kalkusch
 */
public class SystemTime {

	/** 
	 * default String for in case of errors
	 */
	private final String errorString = new String("");
	
	/** 
	 * Constructor
	 */
    public SystemTime() { }

    
    /**
     * returns the system date and system time.
     * Format is (YYYY.MM.DD HH.MM.SS).
     */
    public String getTime()
    {    	
		try {
			return new SimpleDateFormat( "yyyy.MM.dd hh.mm.ss"
						).format(Calendar.getInstance().getTime()).toString();
		} catch (Exception ex) {
		    System.err.println("SystemTime.getTime() failed with "+ex);
		}	
	
		return errorString;
    }

    
    /**
     * returns the system date and system time with milliseconds.
     * Format is (YYYY.MM.DD HH.MM.SS,SSS).
     */
     public String getTimeMillisecond()
    {
		try {
			return new SimpleDateFormat( "yyyy.MM.dd hh.mm.ss ,SSS"
						).format(Calendar.getInstance().getTime()).toString();
		} catch (Exception ex) {
		    System.out.println("SystemTime.getTime() failed with "+ex);
		}	
	
		return errorString;
    }

     /**
     * returns the system date and system time with milliseconds to create 
     * a file-name from.
     * Format is (YYYY.MM.DD HH.MM.SS,SSS).
     */
     public String getTimeMillisecondForFileName()
    {
		try {
			return new SimpleDateFormat( "yyyy-MM-dd_hh-mm-ss.SSS"
						).format(Calendar.getInstance().getTime()).toString();
		} catch (Exception ex) {
		    System.out.println("SystemTime.getTime() failed with "+ex);
		}	
	
		return errorString;
    }
}
