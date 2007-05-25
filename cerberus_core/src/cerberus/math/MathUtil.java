package cerberus.math;

/**
 * 
 * @author Michael Kalkusch
 *
 */
public final class MathUtil {

	/**
	 * define PI as float
	 */
	public static final float PI = (float) Math.PI;
	
	private static final float OPERAND_RAD_2_GRAD = 180.0f / (float) Math.PI;
	
	private static final float OPERAND_GRAD_2_RAD = (float) Math.PI / 180.0f;
	
	private static final double OPERAND_RAD_2_GRAD_DOUBLE = 180.0 / Math.PI;
	
	private static final double OPERAND_GRAD_2_RAD_DOUBLE = Math.PI / 180.0;
	
	/**
	 * Define lowest bits for Application Id
	 */
	private static final int POST_SET_SHIFTER_APP = 2;
	
	/**
	 * Define size of post-Id.
	 * 
	 * Note: [MAX_ID_POSTSET] == 2 ^ [POST_SET_SHIFTER] !
	 */
	private static final int POST_SET_SHIFTER = 10;
		
	/**
	 * Define size of post id.
	 * 
	 * Note: [MAX_ID_POSTSET] == 2 ^ [POST_SET_SHIFTER] !
	 */
	public static final int MAX_ID_POSTSET = 1024;		
	
	
	/**
	 * Private constructor
	 *
	 */
	private MathUtil() { }
	
	
	public static final int cutoffPostId( final int id) {
		
		return id >> POST_SET_SHIFTER;
	}	
	
	protected static final int addPostId( final int id) {
		
		return id << POST_SET_SHIFTER;
	}
	
	/**
	 * Multiplies the value with the postId and returns it.
	 * Converts a integer to an id.
	 * 
	 * @param value
	 * @param postId
	 * @return value * MathUtil.MAX_ID_POSTSET + postId
	 */
	public static final int addPostIdToInteger( final int value, 
			final int postId) {
		
		return (value << POST_SET_SHIFTER) + postId;
	}
	

	public static final int setPostId_Application(final int id, 
			final int applicationId) {
		//TODO: verify this code!
		
		return ((id << POST_SET_SHIFTER_APP) >> POST_SET_SHIFTER_APP) + applicationId;
	}
	
	public static final int setPostId_Type( final int id, 
			final int typeId) {
		//TODO: verify this code!
		
		return id - getPostId_Type(id) + typeId;
	}
	
	public static final int setPostId_Type_Application( final int id, 
			final int typeId,
			final int applicationId ) {
		//TODO: verify this code!
		
		return ((id >> POST_SET_SHIFTER) << POST_SET_SHIFTER) 
			+ applicationId 
			+ typeId << POST_SET_SHIFTER_APP;
	}
	
	public static final int getPostId( final int id) {
		//TODO: verify this code!
		
		return id - ((id >> POST_SET_SHIFTER) << POST_SET_SHIFTER);
	}
	
	public static final int calcualtePostId(final int typeId, 
			final int applicationId) {
		//TODO: verify this code!
		
		return typeId << POST_SET_SHIFTER_APP +  applicationId;
	}
	
	public static final int getPostId_Application( final int id) {
		//TODO: verify this code!
		
		return id - (id << POST_SET_SHIFTER_APP);
	}
	
	public static final int getPostId_Type( final int id) {
		//TODO: verify this code!
		
		// id - (id << POST_SET_SHIFTER ) - getPostId_Application(id);		
		// return id - (id << POST_SET_SHIFTER ) - (id - (id << POST_SET_SHIFTER_APP));
		
		return (id << POST_SET_SHIFTER_APP) - (id << POST_SET_SHIFTER );
	}
	
	public static final boolean testConsitency() {
		
		boolean testSuccessful = true;
		
		int z = MathUtil.cutoffPostId(MathUtil.MAX_ID_POSTSET);
		
		if ( z != 1 ) {
			testSuccessful = false;
			System.err.println("MathUtil.POSTSET_DIVISION does not match private MathUtil.POSTSET_SHIFTER=" + 
					MathUtil.MAX_ID_POSTSET);
		}
		
		int a = 10;
		int b = 7;
		
		int r = MathUtil.addPostId(a);
		int s = MathUtil.MAX_ID_POSTSET * a;
		int t = MathUtil.addPostIdToInteger(a, b);
		int u = MathUtil.getPostId(t);
		int v = MathUtil.cutoffPostId(t);
		
		/* int r = MathUtil.addPostId(a); */
		/* int s = MathUtil.MAX_ID_POSTSET * a; */
		if ( r != s ) {
			testSuccessful = false;
			System.err.println("addPostId(int) failed!");
		}
		
		/* int t = MathUtil.addPostId(a, b); */
		if ( t != (s+b) ) {
			testSuccessful = false;
			System.err.println("addPostId(int,int) failed!");
		}
		
		/* int u = MathUtil.getPostId(t); */		
		if ( u != b ) {
			testSuccessful = false;
			System.err.println("getPostId(int) failed!");
		}
		
		/* int v = MathUtil.cutoffPostId(t); */		
		if ( v != a ) {
			testSuccessful = false;
			System.err.println("cutoffPostId(int) failed!");
		}
		
		int w = MathUtil.setPostId_Type(s, 4);
		int w1= MathUtil.getPostId_Type(w);
		
		/* int v = MathUtil.cutoffPostId(t); */		
		if ( 4 != w1 ) {
			testSuccessful = false;
			System.err.println("setPostId_Type(int,int) and/or getPostId_Type(int) failed!");
		}
		
		int x = MathUtil.setPostId_Application(s, 3);
		
		if ( 3 != MathUtil.getPostId_Application(x) ) {
			testSuccessful = false;
			System.err.println("setPostId_Application(int,int) and/or getPostId_Application(int) failed!");
		}
		
		int y = MathUtil.setPostId_Type_Application(s, 11, 2);
		
		if (( 2 != MathUtil.getPostId_Application(y) )||
				(11 != MathUtil.getPostId_Type(y))) {
			testSuccessful = false;
			System.err.println("setPostId_Type_Application(int,int,int) failed!");
		}		
				
		if ( testSuccessful ) {
			System.out.println("class MathUtil passed all tests!");	
		}
				
		return testSuccessful;
	}
		
	/**
	 * Convert radiant for example: [ 0 .. 2*PI ] to degree [0.. 360°]
	 * Note: if input value fRad is smaller than 0 oder larger than 2+PI the result is also either smalelr than 0° or larger than 360°
	 * 
	 * @param fRad radiant
	 * @return degree
	 */
	public static float radiant2Grad( final float fRad) {
		return fRad * OPERAND_RAD_2_GRAD;
	}
	
	/**
	 * Convert degrees for example: [0.. 360°] to radiant [ 0 .. 2*PI ]
	 * Note: if input value fRad is smaller than 0 oder larger than 2+PI the result is also either smalelr than 0° or larger than 360°
	 * 
	 * @param fDegree degree, that will be converted to radiant
	 * @return radiant
	 */
	public static float grad2radiant( final float fDegree) {
		return fDegree * OPERAND_GRAD_2_RAD;
	}
	
	
	/**
	 * Convert radiant for example: [ 0 .. 2*PI ] to degree [0.. 360°]
	 * Note: if input value fRad is smaller than 0 oder larger than 2+PI the result is also either smalelr than 0° or larger than 360°
	 * 
	 * @param fRad radiant
	 * @return degree
	 */
	public static float radiant2Grad_doublePrecission( final float fRad) {
		return (float) ((double) fRad * OPERAND_RAD_2_GRAD_DOUBLE);
	}
	
	/**
	 * Convert degrees for example: [0.. 360°] to radiant [ 0 .. 2*PI ]
	 * Note: if input value fRad is smaller than 0 oder larger than 2+PI the result is also either smalelr than 0° or larger than 360°
	 * 
	 * @param fDegree degree, that will be converted to radiant
	 * @return radiant
	 */
	public static float grad2radiant_doublePrecission( final float fDegree) {
		return (float) ((double) fDegree * OPERAND_GRAD_2_RAD_DOUBLE);
	}
	
}
