/**
 * 
 */
package cerberus.view.gui.opengl.canvas.isosurface;

// import javax.media.opengl.*;

//import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;
//import gleem.linalg.Vec3d;

import gleem.linalg.open.Vec4fp;


/**
 * @author java
 *
 */
public abstract class SoMarchBase {

	public class SoMaterial {
		public short R;
		public short G;
		public short B;
		public short A;
		
		public SoMaterial() {
			
		}
		
		public SoMaterial( final int R, final int G, final int B, final int A) {
			this.R = (short) R;
			this.G = (short) G;
			this.B = (short) B;
			this.A = (short) A;
		}
	}
	
	// SoMFVec4f *coords;
	protected Vec4fp [] coords;
	
	protected Vec3f [] vertices;
    protected Vec3f [] normals;
    protected int [] normalbinds;
   
    //protected SoMaterial []materials;
    
    protected short [] data;
    
    protected int actvertex;
    protected int actface;
    
    protected int [] dimensions = { 0, 0, 0 };
    
    protected int pointcacheused = 0;
    
    protected int indexcacheused = 0;
    
    /**
     * Index of Indexed Faceset 
     */
    protected int [] faceset;
    
    public static final int INDEX_CACHE_SIZE 		= 24000;
    public static final int  POINT_CACHE_SIZE  		= 24000;
    public static final int  GRADIENT_CACHE_SIZE 	= 24000;
    
    public static final float W_EPSILON 			= 0.0000001f;

    public static final float MB_EPSILON 			= 0.0000001f;
    
    public static final int bit0 =   1;
    public static final int bit1 =   2;
    public static final int bit2 =   4;
    public static final int bit3 =   8;
    public static final int bit4 =  16;
    public static final int bit5 =  32;
    public static final int bit6 =  64;
    public static final int bit7 = 128;
    
	/**
	 * 
	 */
	public SoMarchBase() {
		
	}
	
	protected Vec4fp[] getCubeCoords(final int xpos, final int ypos, final int zpos, final double [] values) 
	{
		Vec4fp [] coords = new Vec4fp [8];
		
	    for ( int level = 0; level < 2; level++ ) 
	    {
	    	coords[ level * 4 ] = new Vec4fp( 
	    			(float) xpos, 
	    			(float) ypos, 
	    			(float) zpos+level,
	    			(float) values[ level * 4 ] );
		  
	    	coords[ level * 4 +1 ]= new Vec4fp( 
	    			(float) xpos+1, 
	    			(float) ypos, 
	    			(float) zpos+level,
	    			(float) values[ level * 4 +1 ] );
	    	
	    	coords[ level * 4 +2 ]= new Vec4fp( 
	    			(float) xpos, 
	    			(float) ypos+1, 
	    			(float) zpos+level,
	    			(float) values[ level * 4 +2 ] );

	    	coords[ level * 4 +3 ]= new Vec4fp( 
	    			(float) xpos+1, 
	    			(float) ypos+1, 
	    			(float) zpos+level,
	    			(float) values[ level * 4 +3 ] );
	    }
		
	    return coords;
	}
	

}
