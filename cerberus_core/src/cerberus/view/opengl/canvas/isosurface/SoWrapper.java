// ===========================================================================
//  (C) 2001 Graz University of Technology
// ===========================================================================
//  NAME:       SoWrapper.h
//  TYPE:       c++ source code
//  PROJECT:    Studierstube
//  CONTENT:    Wrapper Surface extraction algorithm Implementation
//              proposed by André Gueziec (1995)
//  VERSION:    2.0
// ===========================================================================
//  AUTHORS:    ab      Alexander Bornik
// ===========================================================================
//  HISTORY:
//
// ===========================================================================


package cerberus.view.opengl.canvas.isosurface;



/*
#ifndef __WRAPPER__
#define __WRAPPER__

#include <GL/glew.h>

#include "SoMarchBase.h"
#include "LiverPlannerBaseObject.h"

#include <Inventor/nodes/SoCoordinate3.h>
#include <Inventor/nodes/SoIndexedFaceSet.h>


#include <Inventor/fields/SoMFVec4f.h>
#include <Inventor/fields/SoMFInt32.h>
#include <Inventor/nodes/SoSeparator.h>
#include <Inventor/nodes/SoShapeHints.h>
#include <Inventor/nodes/SoNormal.h>
#include <Inventor/nodes/SoScale.h>
#include <Inventor/nodes/SoNormalBinding.h>
#include <Inventor/nodes/SoMaterial.h>
#include <Inventor/actions/SoGLRenderAction.h>
#include <Inventor/actions/SoGetBoundingBoxAction.h>
#include <Inventor/actions/SoGetMatrixAction.h>
#include <Inventor/actions/SoHandleEventAction.h>
#include <Inventor/actions/SoPickAction.h>
#include <Inventor/actions/SoSearchAction.h>

#include <fstream>
#include <volclasses/voldata.h>

#ifndef __APPLE__
#include <GL/gl.h>
#else
#include <OpenGL/gl.h>
#endif
#include <iostream>

#include <ANN/ANN.h>

*/


//import javax.media.opengl.*;
//import javax.media.opengl.glu.*;

//import gleem.*;
//import gleem.linalg.*;

//import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
//import gleem.linalg.Vec3d;
//import gleem.linalg.Vec4f;

import gleem.linalg.open.Vec4fp;

import cerberus.view.opengl.canvas.isosurface.SoMarchBase;

public class SoWrapper extends SoMarchBase
//: public SoMarchBase, public LiverPlannerBaseObject
{

	private int [] edge_0;
	private int [] edge_3;
	private int [] edge_16;
	private int [] edge_22;
    
	private int [] edge_4;
	private int [] edge_8;
	private int [] edge_12;
	private int [] edge_18;
    
	private int edge_7;
	private int edge_11;
	private int edge_15;
	private int edge_21;
    
	private short isovalue;
	
	private int nTriang = 0;
	
	private int nPoints = 0;
	
	private static final int iFaceDimension_VertexIndexPerFace = 3;
	
	private static final int iVertexDimension = 3;
	
	protected static final  int[][] edge_lut = 
		{
		{ -1,  0,  3, 16,  8, 12, 15, -1 },
		{  0, -1, 22,  1, 18,  9, -1, 13 },
		{  3, 22, -1,  2, 21, -1, 11, 14 },
		{ 16,  1,  2, -1, -1, 19, 20, 10 },
		{  8, 18, 21, -1, -1,  4,  7, 17 },
		{ 12,  9, -1, 19,  4, -1, 23,  5 },
		{ 15, -1, 11, 20,  7, 23, -1,  6 },
		{ -1, 13, 14, 10, 17,  5,  6, -1 }
		};
	

	protected static final short [] apex_even = { 0x0, 0x3, 0x5, 0x6, 0x1 };
	protected static final short [] apex_odd  = { 0x1, 0x2, 0x4, 0x7, 0x0 };

	// [5][3]
	protected static final short [][] vertices_even = 
	   { 
	      { 0x1, 0x2, 0x4 },
	      { 0x2, 0x1, 0x7 },
	      { 0x4, 0x7, 0x1 },
	      { 0x7, 0x4, 0x2 },
	      { 0x2, 0x4, 0x7 }
	   };

	// [5][3]
	protected static final short [][] vertices_odd = 
	   { 
	      { 0x0, 0x3, 0x5 },
	      { 0x3, 0x0, 0x6 },
	      { 0x5, 0x6, 0x0 },
	      { 0x6, 0x5, 0x3 },
	      { 0x3, 0x5, 0x6 }
	   };
	
	// [16]
	protected static final boolean [] dets =
	   {  true,  true, false,  false,  
		true, false,  false,  true, 
		false,  true, true, false,  
		true,  true, false,  true };
	
	// { false, false,  true, false, false,  true, false, false,  true, false,  true,  true, false, false,  true, false };
	// [5][4][4]
	protected static final int [][][] ntet_even =
	   {
	      {
	         { -1,  0,  0,  1 },
	         {  0, -1,  0,  2 },
	         {  0,  0, -1,  3 },
	         {  0,  0,  0,  5 }
	      },
	      {
	         {  1,  0,  0,  2 },
	         {  0,  1,  0,  1 },
	         {  0,  0, -1,  4 },
	         {  0,  0,  0,  5 },
	      },
	      {
	         {  1,  0,  0,  3 },
	         {  0, -1,  0,  4 },
	         {  0,  0,  1,  1 },
	         {  0,  0,  0,  5 }		
	      },
	      {
	         { -1,  0,  0,  4 },
	         {  0,  1,  0,  3 },
	         {  0,  0,  1,  2 },
	         {  0,  0,  0,  5 }
	      },
	      {
	         {  0,  0,  0,  1 },
	         {  0,  0,  0,  2 },
	         {  0,  0,  0,  3 },
	         {  0,  0,  0,  4 }
	      }
	   };

	//[5][4][4]
	protected static final short [][][] ntet_odd =
	   {
	      {
	         {  1,  0,  0,  1 },
	         {  0, -1,  0,  2 },
	         {  0,  0, -1,  3 },
	         {  0,  0,  0,  5 }
	      },
	      {
	         { -1,  0,  0,  2 },
	         {  0,  1,  0,  1 },
	         {  0,  0, -1,  4 },
	         {  0,  0,  0,  5 },
	      },
	      {
	         { -1,  0,  0,  3 },
	         {  0, -1,  0,  4 },
	         {  0,  0,  1,  1 },
	         {  0,  0,  0,  5 }		
	      },
	      {
	         {  1,  0,  0,  4 },
	         {  0,  1,  0,  3 },
	         {  0,  0,  1,  2 },
	         {  0,  0,  0,  5 }
	      },
	      {
	         {  0,  0,  0,  1 },
	         {  0,  0,  0,  2 },
	         {  0,  0,  0,  3 },
	         {  0,  0,  0,  4 }
	      }
	   };


	// [16][3]
	protected static final short [][] sloc_edges = 
	   { 
	      { 0, 0, 0 },
	      { 1, 2, 0 },
	      { 2, 0, 1 },
	      { 0, 2, 1 },
	      { 0, 1, 2 },
	      { 1, 0, 2 },
	      { 2, 1, 0 },
	      { 0, 0, 0 },
	      { 0, 0, 0 },
	      { 2, 1, 0 },
	      { 1, 0, 2 },
	      { 0, 1, 2 },
	      { 0, 2, 1 },
	      { 2, 0, 1 },
	      { 1, 2, 0 },
	      { 0, 0, 0 }
	   };
	
	/**
	 * PROTECTED VIRABELS
	 */
	

	
	public SoWrapper() 
	{
		
				   
	}
	
	
//	public SoWrapper(int numChildren);
//	public SoWrapper(const char *fname, const char *origfn = NULL, 
//              int maxxsize = -1, int maxysize = -1, int maxzsize = -1, 
//              bool dilate = false);
//	public ~SoWrapper();
//	
	
	public void setDimensions( int [] setDimensions ) {
		
		if ( setDimensions.length < 2 ) {
			cout("setDimensions() dimensions must be at least 3 dimensional!");
			return;
		}
		
		cout(" --- set dimensions ---");
		
		for ( int i=0; i< 3; i++) {
			this.dimensions[i] = setDimensions[i];
			
			cout("  " + i + "# " + dimensions[i] );
		}
	}

	public void march( final int threshold ) {
	
		/**
		 * Create point cache buffer
		 */
		float [][] pointcache = 
			new float[POINT_CACHE_SIZE][3];

		int [] indexcache =
			new int [INDEX_CACHE_SIZE];
		
		   if ( data == null ) {
			   cout("ERROR! no data is set!");
			   return;
		   }
		   
		   
		   // using namespace std;
		   
		   try 
		   {
			   isovalue = (short) threshold;
		   }
		   catch (RuntimeException re) 
		   {
				cout("Can not convert threshold to (short)!");
				throw new RuntimeException("Can not convert threshold to (short)!");
		   }

		   coords = new Vec4fp [8];
		   
		   //coords->setNum(8);

		   // unlink from inventor
		   //vertices->point.startEditing();
		   //faceset->coordIndex.startEditing();
		   //faceset->materialIndex.startEditing();
		   //materials->diffuseColor.startEditing();

		   // delete old values;

		   int iSizeNormals = 2500;
		   int iSizeVertices = 2500; //2500;
		   int iSizeIndexedFaceSet = 50000;
		   
		   vertices = new Vec3f [iSizeVertices];
		   
		   faceset = new int [iSizeIndexedFaceSet];
		   
		   normals = new Vec3f [iSizeNormals];
		   
//		   materials->diffuseColor.setNum(0);
//		   materials->transparency.setNum(0);
//		   materials->specularColor.set1Value(0, gspecular);
//		   materials->transparency.set1Value(0, 1.0-gtransparency);

		   // reset index cache and point cache

		   int i,j;

		   for (i=0; i<POINT_CACHE_SIZE; i++)
		   {
		      pointcache[i][0]= -1.0f;
		      pointcache[i][1]= -1.0f;
		      pointcache[i][2]= -1.0f;
		      
//		      materialcache[i][0]= 1.0;
//		      materialcache[i][1]= 1.0;
//		      materialcache[i][2]= 1.0;
		   }

		   indexcacheused=0;
		   pointcacheused=0;
			
		   /**
		    * current index of vertex
		    */
		   actvertex=0;
		   
		   /**
		    * current index of face
		    */
		   actface=0;

		   int  x, y, z;
		   
		   /**
		    * Flipping tedrahedrons...
		    */
		   boolean even;
		   int actindex, yincrement, zincrement;
		   int offset;

		   int cubeindex;

		

		   yincrement = dimensions[0];
		   zincrement = dimensions[0]*dimensions[1];

		   /**
		    *  Swapping and comparing 8 voxel data values 
		    */
		   double [] cornervalues = new double[8];

		   /**
		    * Swap buffer assigned once per marching operation
		    */
		   // reserve edge LUTs
		   edge_0 = new int[dimensions[0]*dimensions[1]];
		   edge_3 = new int[dimensions[0]*dimensions[1]];
		   edge_16 = new int[dimensions[0]*dimensions[1]];
		   edge_22 = new int[dimensions[0]*dimensions[1]];	

		   edge_4 = new int[dimensions[0]];
		   edge_8 = new int[dimensions[0]];
		   edge_12 = new int[dimensions[0]];
		   edge_18 = new int[dimensions[0]];

		   /**
		    * Init LUT
		    */
		   for (j=0; j< dimensions[1]; j++) 
		      for (i=0; i< dimensions[0]; i++)
		      {	
		         edge_0[dimensions[0]*j+i] = -1;
		         edge_3[dimensions[0]*j+i] = -1;
		         edge_16[dimensions[0]*j+i] = -1;
		         edge_22[dimensions[0]*j+i] = -1;
		      }

		   
		   // walk through volume

		cout( "Starting Isosurfacing ..." );
			
		   for (z=0; z < dimensions[2] -1; z++)
		   {

			   cout( "Level " + z + " of " + (dimensions[2] -1) );
		
		      /*
		        for (j=0; j< dimensions[1]-1; j++) 
		        for (i=0; i< dimensions[0]-1; i++)
		        {
		        edge_
		        }
		      */
			   
			   /**
			    * Reset LUT for current layer
			    */
		      for (i=0; i< dimensions[0]; i++)
		      {	
		         edge_4[i] = -1;
		         edge_8[i] = -1;
		         edge_12[i] = -1;
		         edge_18[i] = -1;
		      }	

		      
		      for (y=0; y < dimensions[1]-1; y++)
		      {
		    	  /**
		    	   * Reset values...
		    	   */
		         edge_7 = -1;
		         edge_11 = -1;
		         edge_15 = -1;
		         edge_21 = -1;

		         
		         for (x=0; x < dimensions[0]-1; x++)
		         {
		        	 /**
		        	  * Calculate position in array...
		        	  */
		            actindex = x + y*yincrement + z*zincrement;
		            offset = actindex;

		            //cerr << ((x+y+z)%2) << endl;

		            /**
		             * Falg for flipping tetrahedron...
		             */
		            if (((x+y+z)%2) == 1)
		               even = true;
		            else
		               even = false;

		            /**
		             * cubeindex = 0 .... indicate not part of iso-surface
		             * 
		             * valid range: [0..255]
		             */
		            cubeindex = 0;				
						
		            /**
		             * Compare current value with iso value
		             * Set bits.
		             */
		            if ( data[ offset ] >=  isovalue ) 
		            { 
		               cubeindex |= bit0;										
		            }
		            cornervalues[0] = data[ offset ];
		            
		            if ( data[ offset + zincrement ] >=  isovalue )
		            {
		               cubeindex |= bit4;
		            }
		            cornervalues[4] = data[ offset + zincrement ];
		            offset++;
			      
		            if ( data[ offset ] >=  isovalue ) 
		            {
		               cubeindex |= bit1;
		            }
		            cornervalues[1] = data[ offset ];
		            if ( data[ offset + zincrement] >=  isovalue ) 
		            {
		               cubeindex |= bit5;
		            }
		            cornervalues[5] = data[ offset + zincrement ];
		            offset = actindex + yincrement;
						
			      
		            if ( data[ offset ] >=  isovalue )
		            {
		               cubeindex |= bit2;					
		            }
		            cornervalues[2] = data[ offset ];
		            
		            if ( data[ offset + zincrement] >=  isovalue) 
		            {
		               cubeindex |= bit6;	
		            }
		            cornervalues[6] = data[ offset + zincrement ];
		            offset++;

		            if ( data[ offset ] >=  isovalue ) 
		            {
		               cubeindex |= bit3;
		            }
		            cornervalues[3] = data[ offset ];
		            
		            if ( data[ offset + zincrement ] >=  isovalue) 
		            {
		               cubeindex |= bit7;
		            }
		            cornervalues[7] = data[ offset + zincrement ];

		            //if ((cubeindex != 0) && (cubeindex != 255))
		            //	cerr << x << ", " << y << ", " << z << " : cubeindex " << cubeindex << endl;
						
		            /**
		             * Compare current value with iso value DONE
		             * All bits are set
		             */
		            
		            /**
		             * Read cases...
		             */
		            
		            /**
		             * Fully outside (cubeindex==0) or fully inside (cubeindex==255)
		             */
		            if ((cubeindex != 0) && (cubeindex != 255))
		            {
		               //double* iso = getCubeIsovalues(actindex, cornervalues);

		            	cout(" inside!  isovalue=[" + isovalue + "]");
		            	
		            	Vec4fp [] isoCoords = getCubeCoords(x, y, z, cornervalues);

		               //int ntriang;
		               //int npoints = 0;
							
		               if (pointcacheused >= (POINT_CACHE_SIZE-18*iVertexDimension-1) )
		               {
		            	   
		            	   for ( int m=0; m < pointcacheused; m++) 
		            	   {
		            		   vertices[actvertex].set(
		            				   pointcache[m][0],
		            				   pointcache[m][1],
		            				   pointcache[m][2] );
		            		   
		            		   actvertex++;
		            		   //actvertex += pointcacheused;
		            	   }
		
		                  //materials->diffuseColor.setValues(actvertex, pointcacheused, materialcache);
		
		                  pointcacheused = 0;
		                  //cerr << "flushing point cache" << endl;
		               }

		               if (indexcacheused >= ( INDEX_CACHE_SIZE-18*iFaceDimension_VertexIndexPerFace-1) )		               
		               {
		                  
		            	   // ( start, counter, array[] )
		            	   //faceset->coordIndex.setValues(actface, indexcacheused, indexcache);		           		
		            	   for ( int m=0; m < indexcacheused; m++) 
		            	   {
		            		   faceset[actface] =
		            				   indexcache[m];
		            		   
		            		   actface++;
		            		   //actface += indexcacheused;
		            	   }
		            	   
		            	  
		                  //faceset->materialIndex.setValues(actface, indexcacheused, indexcache);
		                  
		                  indexcacheused = 0;
		               }
							
		               nTriang = PolygonizeGrid(x,y, isoCoords, 
		                                        actvertex+pointcacheused, 
		                                        pointcache, 
		                                        actface, 
		                                        // &indexcache[indexcacheused], 
		                                        indexcache,
		                                        indexcacheused,		                                        
		                                        // use global: npoints, 
		                                        nTriang,
		                                        even);

							
		               //int k;
		               /*cerr << "------------" << endl;
		                 for (k=0; k< npoints; k++)
		                 {
		                 cerr << pointcache[pointcacheused+k][0] << " ";
		                 cerr << pointcache[pointcacheused+k][1] << " ";
		                 cerr << pointcache[pointcacheused+k][2] << " ";
		                 cerr << endl;
		                 }*/
		               pointcacheused += this.nPoints;
		               //cerr << pointcacheused << ", " << npoints << endl;
		               /*
		                 if (even)
		                 cerr << x << ", " << y << ", " << z << " *" << endl;
		                 else
		                 cerr << x << ", " << y << ", " << z << "." << endl;
		               */
		               /*
		                 int k;
		                 for (k=0; k<ntriang; k++)
		                 {
		                 if (even)
		                 {
		                 materials->diffuseColor.set1Value(indexcacheused/4+actface/4+k, 1.0, 1.0, 0.0);	
		                 }
		                 else
		                 {	
		                 materials->diffuseColor.set1Value(indexcacheused/4+actface/4+k, 0.0, 1.0, 0.0);
		                 }
		                 }
		               */
		               indexcacheused = nTriang * iFaceDimension_VertexIndexPerFace;					

							
		               /*
							
		               int count = 0;
		               if (ntriang*4 >0)
		               {
		               for (k=0; k< ntriang*4; k++)
		               //if (indexcache[indexcacheused+k] >= actvertex+pointcacheused)
		               {
		               cerr << "ERROR: " << indexcache[indexcacheused-ntriang*4+k] << ", "
		               <<	actvertex+pointcacheused << endl;
								
		               }
		               count++;
		               if (count > 5) exit(1);
		               }*/

		            }  // not full or empty cube		
		            else
		            {
		               /*
		                 edge_0[y*dimensions[0]+x] = -1;
		                 edge_3[y*dimensions[0]+x] = -1;
		                 edge_16[y*dimensions[0]+x] = -1;
		                 edge_22[y*dimensions[0]+x] = -1;

		                 edge_4[x] = -1;
		                 edge_8[x] = -1;
		                 edge_12[x] = -1;
		                 edge_18[x] = -1;

		                 edge_7 = -1;
		                 edge_11 = -1;
		                 edge_15 = -1;
		                 edge_21 = -1;
		               */
		            }// if ((cubeindex != 0) && (cubeindex != 255)) {..} else {..}
		            
		         } //for (x=0; x < dimensions[0]-1; x++)
		         
		      } // for (y=0; y < dimensions[1]-1; y++)
		      
		      cout( "Level " + z + " of " + (dimensions[2] -1) + "  [done]");
		      
		   } // for (z=0; z < dimensions[2] -1; z++)

		   cout("IsoSurface done!");

		   if (indexcacheused > 0)
		   {		      
		      //faceset->materialIndex.setValues(actface, indexcacheused, indexcache);
		
		      for ( int m=actface; m < (indexcacheused+actface); m++) 
	       	   {
	       		   faceset[m] = indexcache[m];	       		  
	       	   }
		      
		      actface += indexcacheused;
       	   
		      indexcacheused = 0;
		   }

		   if (pointcacheused > 0)
		   {
		      //vertices->point.setValues(actvertex, pointcacheused, pointcache);
	
		      for ( int m=actvertex; m < (pointcacheused+actvertex); m++) 
	       	   {
//	       		   vertices[actvertex].set(
//	       				   pointcache[m][0],
//	       				   pointcache[m][1],
//	       				   pointcache[m][2] );
	       		   
		    	  vertices[m] = new Vec3f(
	       				   pointcache[m][0],
	       				   pointcache[m][1],
	       				   pointcache[m][2] );
	       	   }
		      
		      actvertex += pointcacheused;
		      
		      //materials->diffuseColor.setValues(actvertex, pointcacheused, materialcache);
		
		      pointcacheused = 0;
		   }

		
//		   cout( "total # of points: ?" + vertices->point.getNum() );
//		   cout( "total # of faces: ? " + faceset->coordIndex.getNum()/4  );
		   
		   cout( "total # of points: ? " + this.actvertex );
		   cout( "total # of faces: ? " + this.actface );
		   
		   // cout( "total # of face reference lists: " + vt_face_list.size() );
		   
		   //cout( "total # of materials: " + materials->diffuseColor.getNum() );
		

		   //int k;
		   
//		   for (k=0; k<faceset->coordIndex.getNum(); k++)
//		      if (faceset->coordIndex[k] > vertices->point.getNum()-1)
//		      {
//		         cerr << "ERROR " << endl;
//		      }

		   // everything set for rendering

		   //vertices->point.finishEditing();
		   //faceset->coordIndex.finishEditing();
		   //faceset->materialIndex.finishEditing();
		   //materials->diffuseColor.finishEditing();

		   /**
		    * delete temporary values
		    */

//		   delete [] cornervalues;
//
//		   delete [] edge_0;
//		   delete [] edge_3;
//		   delete [] edge_16;
//		   delete [] edge_22;
//
//		   delete [] edge_4;
//		   delete [] edge_8;
//		   delete [] edge_12;
//		   delete [] edge_18;
		   
		   cornervalues = null;

		   edge_0 = null;
		   edge_3 = null;
		   edge_16 = null;
		   edge_22 = null;

		   edge_4 = null;
		   edge_8 = null;
		   edge_12 = null;
		   edge_18 = null;
	}
	
	public void readBackIsosurface() {
	
		   cout( "total # of points: ? " + this.actvertex );
		   cout( "total # of faces: ? " + this.actface );
		   
		   //this.vertices
		   
	}
	
	public boolean hasValidIsosurface() {
		if ( actface > 0 ) {
			return true;
		}
		return false;
	}
	
	public int getVertexSize() {
		return actvertex;
	}
	
	public int getFaceSetSize() {
		return actface;
	}
	
	public Vec3f[] getVertices() {
		
		if ( actvertex > 1 ) 
		{
			return this.vertices;
		}
		
		return null;
	}
	
	
	public int[] getFaceSet() {
		
		if ( actface > 1 ) 
		{
			return this.faceset;
		}
		
		return null;
	}
	
	
	public void assignData( final short [] sDataArray ) {
		this.data = sDataArray;
	}
	
	
	protected int PolygonizeGrid( int xp, 
			int yp, 
			Vec4fp [] grid,  
            int startpoint, 
            // float points[POINT_CACHE_SIZE][3], 
            float points[][], 
            int startface,
            //int faces[18*4], 
            int faces[],
            final int facesIndex,
            // int *npts,
            // use global value! int npts, 
            final int tnum_current,
            boolean even)
	{
		int tnum= tnum_current;
		//int pnum=0;
		int lpts=0;
		
		double [][] p_double_array2D = new double[4][3];
		int [][] t_int_array2D = new int [3][3];
		int x;
		
		int [] inters_flag = new int [24];
		int [] flag_touched = new int [24];
		int i;
		
		for (i=0; i<24; i++)
		{
			flag_touched[i] = 0;
		}
		
		// set up intersection flags according to stored values;
		
		inters_flag[0] = edge_0[yp*dimensions[0]+xp];
		inters_flag[1] = edge_3[yp*dimensions[0]+(xp+1)];
		inters_flag[2] = edge_0[(yp+1)*dimensions[0]+xp];
		inters_flag[3] = edge_3[yp*dimensions[0]+xp];
		inters_flag[4] = edge_4[xp];
		inters_flag[5] = -1;
		inters_flag[6] = -1;
		inters_flag[7] = edge_7;
		inters_flag[8] = edge_8[xp];
		inters_flag[9] = edge_8[xp+1];
		inters_flag[10] = -1;
		inters_flag[11] = edge_11;
		inters_flag[12] = edge_12[xp];
		inters_flag[13] = -1;
		inters_flag[14] = -1;
		inters_flag[15] = edge_15;
		inters_flag[16] = edge_16[yp*dimensions[0]+xp];
		inters_flag[17] = -1;
		inters_flag[18] = edge_18[xp];
		inters_flag[19] = -1;
		inters_flag[20] = -1;
		inters_flag[21] = edge_21;
		inters_flag[22] = edge_22[yp*dimensions[0]+xp];
		inters_flag[23] = -1;
		
		int n;
		
		// calculate faces for all 5 tetrahedrons a cube consists of
		
		int tet;
		for (tet = 0; tet < 5; tet++)
		{
			if (even)
			{
			n = PolygoniseTri(grid, 
					isovalue, 
					inters_flag, 
					flag_touched, 
					t_int_array2D, 
					p_double_array2D, 
			        apex_even[tet], 
			        vertices_even[tet][0], 
			        vertices_even[tet][1], 
			        vertices_even[tet][2], 
			        startpoint+lpts,
			        //int &npts,
			        //use global! pnum, 
			        even);
			}
			else
			{
			n = PolygoniseTri(grid, 
					(float) isovalue, 
					inters_flag, 
					flag_touched, 
					t_int_array2D, 
					p_double_array2D, 
			        apex_odd[tet], 
			        vertices_odd[tet][0], 
			        vertices_odd[tet][1], 
			        vertices_odd[tet][2],
			        startpoint+lpts,
			        //pnum, 
			        even);
			}
		
			// add generated points to point cache
			//for (x=0;x < pnum; x++)
			for (x=0;x < this.nPoints; x++)
			{
				points[pointcacheused+lpts][0] = (float) p_double_array2D[x][0];
				points[pointcacheused+lpts][1] = (float) p_double_array2D[x][1];
				points[pointcacheused+lpts][2] = (float) p_double_array2D[x][2];
				
//				materialcache[pointcacheused+lpts][0] = gdiffuse.getValue()[0];
//				materialcache[pointcacheused+lpts][1] = gdiffuse.getValue()[1];
//				materialcache[pointcacheused+lpts][2] = gdiffuse.getValue()[2];
				lpts++;
			}
		
			for (x=0;x<n;x++)
			{
				if ( (t_int_array2D[x][0] != t_int_array2D[x][1]) && 
						(t_int_array2D[x][0] != t_int_array2D[x][2]) &&
						(t_int_array2D[x][1] != t_int_array2D[x][2]) )
				{
//					faces[tnum*4] = t_int_array2D[x][0];
//					faces[tnum*4+1] = t_int_array2D[x][1];
//					faces[tnum*4+2] = t_int_array2D[x][2];
//					faces[tnum*4+3] = -1;
					
					faces[tnum*iFaceDimension_VertexIndexPerFace] = t_int_array2D[x][0];
					faces[tnum*iFaceDimension_VertexIndexPerFace+1] = t_int_array2D[x][1];
					faces[tnum*iFaceDimension_VertexIndexPerFace+2] = t_int_array2D[x][2];
					//faces[tnum*iFace_VertexPerFace+3] = -1;
					
					tnum++;	
				}	
				else
				{
					cout( "Triangle with same indices!" );
					//throw new RuntimeException("Triangle with same indices!");
				} // if ( (t[x][0] != t[x][1]) && (t[x][0] != t[x][2]) && (t[x][1] != t[x][2]) )
				
			} // for (x=0;x<n;x++)
			
		} // for (tet = 0; tet < 5; tet++)
		
		//use global: npts = lpts;
		this.nPoints = lpts;
		
		
		/*
		 * for (i=0; i<24; i++) { if (flag_touched[i] == 0) inters_flag[i] = -1; }
		 */
		
		
		// set new edge coherency values
		
		edge_0[yp*dimensions[0]+xp] = edge_4[xp];
		edge_4[xp] = inters_flag[6];
		
		edge_3[yp*dimensions[0]+xp] = edge_7;
		edge_7 = inters_flag[5];
		
		edge_8[xp] = edge_11;
		edge_11 = inters_flag[10];
		
		edge_12[xp] = inters_flag[14];
		edge_18[xp] = inters_flag[20];
		edge_15 = inters_flag[13];
		edge_21 = inters_flag[19];
		edge_16[yp*dimensions[0]+xp] = inters_flag[17];
		edge_22[yp*dimensions[0]+xp] = inters_flag[23];
		
		return tnum;
		
	} // protected int PolygonizeGrid( int xp, 
	
	
	protected int PolygoniseTri(Vec4fp [] grid,
			float iso, 
			// int elut[24],
			// int touched[24],
			// int triangles[3][3],
			// double pts[4][3],
			int elut[],
			
			// alter
			int touched[],
            int triangles[][], 
            double pts[][],
            int v0, 
            int v1,
            int v2,
            int v3, 
            int poffset, 
            // int &npts,
            //use global!  int npts, 
            boolean even)
	{
		
		int nTrianglesLocal = 0;
		int npt = 0;
		int triindex;
		int zcount =0;
		int negcount =0;
		
		/**
		 * Determine which of the 16 cases we have given which vertices are above or
		 * below the isosurface
		 */
		
		triindex = 0;
		if (grid[v0].w >= iso) 
		{
			triindex |= 1;
			if (grid[v0].w == iso)
			zcount++;
		}
		else
		negcount++;
		
		if (grid[v1].w >= iso) 
		{
			triindex |= 2;
			if (grid[v1].w == iso)
			zcount++;
		}
		else
		{
		    negcount++;
		}
		
		 if (grid[v2].w >= iso) 
		 {
		    triindex |= 4;
		    if (grid[v2].w == iso)
		       zcount++;
		 }
		 else
		    negcount++;
		 
		 if (grid[v3].w >= iso) 
		 {
		    triindex |= 8;
		    if (grid[v3].w == iso)
		       zcount++;
		 }
		 else
		    negcount++;
		
		 /**
			 * check special cases, where some of the tetrahedron's vertices match the
			 * iso value exactly, and some are outside the volume 3 x == 0 and one
			 * negative value -> special case
			 */
		
		 if ((zcount+negcount) == 4)
		 {
		    if (zcount == 3) 
		    {
		    	System.out.print("+");
		       cout( " special case" );
		    }
		    else
		    {
		    	this.nPoints = npt;
		       // use global! npts = npt;
		    	
		       return 0;
		    }
		 }
		 else {
			 System.out.print(".");
			 //cout( " regular case" );
		 }

	
	
	/** 
     * Form the vertices of the triangles for each case 
     */

	switch (triindex) {
	   case 0x00:
	   case 0x0F:
	      break;
		
	   case 0x0E:
	   case 0x01:
		   //triInterp(grid, iso, v0, v1, elut, touched, &pts[npt][0], poffset, npt);
		   npt = triInterp(grid, iso, v0, v1, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v0, v2, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v0, v3, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) // **
	      {
	         triangles[0][0] = elut[edge_lut[v0][v1]];
	         triangles[0][1] = elut[edge_lut[v0][v3]];
	         triangles[0][2] = elut[edge_lut[v0][v2]];	
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 0.0, 1.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v0][v1]];
	         triangles[0][1] = elut[edge_lut[v0][v2]];
	         triangles[0][2] = elut[edge_lut[v0][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
			
	      nTrianglesLocal++;
	      break;
	
	   case 0x0D:
	   case 0x02:
		   //triInterp(grid, iso, v1, v0, elut, touched, &pts[npt][0], poffset, npt);
	      npt = triInterp(grid, iso, v1, v0, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v1, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v1, v2, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) 
	      {
	         triangles[0][0] = elut[edge_lut[v1][v0]];
	         triangles[0][1] = elut[edge_lut[v1][v3]];
	         triangles[0][2] = elut[edge_lut[v1][v2]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v1][v0]];
	         triangles[0][1] = elut[edge_lut[v1][v2]];
	         triangles[0][2] = elut[edge_lut[v1][v3]];		
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	
	      nTrianglesLocal++;
	      break;
		
	   case 0x0C:
	   case 0x03:
	      //triInterp(grid, iso, v0, v3, elut, touched, &pts[npt][0], poffset, npt);
		   npt = triInterp(grid, iso, v0, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v0, v2, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v1, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v1, v2, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) 
	      {
	         triangles[0][0] = elut[edge_lut[v0][v3]];
	         triangles[0][1] = elut[edge_lut[v1][v3]];
	         triangles[0][2] = elut[edge_lut[v0][v2]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 0.0, 0.0);
//	#endif
	
	         triangles[1][0] = elut[edge_lut[v0][v2]];
	         triangles[1][1] = elut[edge_lut[v1][v3]];
	         triangles[1][2] = elut[edge_lut[v1][v2]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 0.0, 0.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v0][v3]];
	         triangles[0][1] = elut[edge_lut[v0][v2]];
	         triangles[0][2] = elut[edge_lut[v1][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 1.0, 1.0);
//	#endif
	
	         triangles[1][0] = elut[edge_lut[v1][v3]];
	         triangles[1][1] = elut[edge_lut[v0][v2]];
	         triangles[1][2] = elut[edge_lut[v1][v2]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 1.0, 1.0);
//	#endif
	      }
	
	      nTrianglesLocal +=2;
			
	      break;
			
	   case 0x0B:
	   case 0x04:
	      //triInterp(grid, iso, v2, v0, elut, touched, &pts[npt][0], poffset, npt);
		  npt = triInterp(grid, iso, v2, v0, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v1, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v3, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) // **
	      {
	         triangles[0][0] = elut[edge_lut[v2][v0]];
	         triangles[0][1] = elut[edge_lut[v2][v3]];
	         triangles[0][2] = elut[edge_lut[v2][v1]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	      else
	      {	
	         triangles[0][0] = elut[edge_lut[v2][v0]];
	         triangles[0][1] = elut[edge_lut[v2][v1]];
	         triangles[0][2] = elut[edge_lut[v2][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	
	      nTrianglesLocal++;
	      break;
			
	   case 0x0A:
	   case 0x05:
	      //triInterp(grid, iso, v0, v1, elut, touched, &pts[npt][0], poffset, npt);
		  npt = triInterp(grid, iso, v0, v1, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v0, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v1, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) // **
	      {
	         triangles[0][0] = elut[edge_lut[v0][v1]];
	         triangles[0][1] = elut[edge_lut[v2][v3]];
	         triangles[0][2] = elut[edge_lut[v0][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 1.0, 0.0);
//	#endif
	         triangles[1][0] = elut[edge_lut[v0][v1]];
	         triangles[1][1] = elut[edge_lut[v2][v1]];
	         triangles[1][2] = elut[edge_lut[v2][v3]];			
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 1.0, 0.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v0][v1]];
	         triangles[0][1] = elut[edge_lut[v0][v3]];
	         triangles[0][2] = elut[edge_lut[v2][v3]];			
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 0.0, 1.0);
//	#endif
	
	         triangles[1][0] = elut[edge_lut[v0][v1]];
	         triangles[1][1] = elut[edge_lut[v2][v3]];
	         triangles[1][2] = elut[edge_lut[v2][v1]];			
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 0.0, 1.0);
//	#endif
	      }
	
	      nTrianglesLocal+=2;
	      break;
	   case 0x09:
	   case 0x06:
	      //triInterp(grid, iso, v1, v0, elut, touched, pts[npt][0], poffset, npt);
		  npt = triInterp(grid, iso, v1, v0, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v1, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v3, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v2, v0, elut, touched, pts[npt], poffset, npt);
	
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) // **
	      {
	         triangles[0][0] = elut[edge_lut[v1][v0]];
	         triangles[0][1] = elut[edge_lut[v1][v3]];
	         triangles[0][2] = elut[edge_lut[v2][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 0.0, 1.0);
//	#endif
	
	         triangles[1][0] = elut[edge_lut[v1][v0]];
	         triangles[1][1] = elut[edge_lut[v2][v3]];
	         triangles[1][2] = elut[edge_lut[v2][v0]];			
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 0.0, 0.0, 1.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v1][v0]];
	         triangles[0][1] = elut[edge_lut[v2][v3]];
	         triangles[0][2] = elut[edge_lut[v1][v3]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 0.0);
//	#endif
	
	         triangles[1][0] = elut[edge_lut[v2][v3]];
	         triangles[1][1] = elut[edge_lut[v1][v0]];
	         triangles[1][2] = elut[edge_lut[v2][v0]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 0.0);
//	#endif
	      }
	      nTrianglesLocal += 2;
	      break;	
	   case 0x07:
	   case 0x08:
		  //triInterp(grid, iso, v3, v0, elut, touched, &pts[npt][0], poffset, npt);
	      npt = triInterp(grid, iso, v3, v0, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v3, v1, elut, touched, pts[npt], poffset, npt);
	      npt = triInterp(grid, iso, v3, v2, elut, touched, pts[npt], poffset, npt);
			
	      if (( (even==true) && (dets[triindex]==false)) || 
	          ( (even==false) && (dets[triindex]==true)) ) // **
	      {
	         triangles[0][0] = elut[edge_lut[v3][v0]];
	         triangles[0][1] = elut[edge_lut[v3][v2]];
	         triangles[0][2] = elut[edge_lut[v3][v1]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	      else
	      {
	         triangles[0][0] = elut[edge_lut[v3][v0]];
	         triangles[0][1] = elut[edge_lut[v3][v1]];
	         triangles[0][2] = elut[edge_lut[v3][v2]];
//	#ifdef __COLORTET__
//	         materials->diffuseColor.set1Value(materials->diffuseColor.getNum(), 1.0, 1.0, 1.0);
//	#endif
	      }
	      nTrianglesLocal++;
	      break;
	}
	
// check for errors 

//	#ifdef __DEBUG__
//	   if ((triangles[0][0] == -1) ||
//	       (triangles[0][1] == -1) ||
//	       (triangles[0][2] == -1)
//	       )
//	   {	
//	      cerr << "ERROR 0" << endl;
//	      exit(1);
//	   }
//	   if (((triangles[0][0] == -1) ||
//	        (triangles[0][1] == -1) ||
//	        (triangles[0][2] == -1) ||
//	        (triangles[1][0] == -1) ||
//	        (triangles[1][1] == -1) ||
//	        (triangles[1][2] == -1)) && (ntriang > 1))
//	   {
//	      cerr << "ERROR 1: ntriang: " << ntriang << endl;
//	      exit(1);
//	   }
//	#endif

	   //use global! npts = npt;
	   this.nPoints = npt;
	   
	   return nTrianglesLocal;
	}

	
	protected int triInterp( 
			Vec4fp [] grid, 
			final float iso, 
			final int vt0, 
			final int vt1, 
            int elut[], 
            int touched[], 
            // variable!
            double pt[], 
            final int offset,
            //  int &npt
            final int npt)
	{
		/**
		 * Check consistency of input data..
		 * 
		 * int elut[24], 
         * int touched[24], 
         * double pt[3], 
		 */
		
		if (elut[edge_lut[vt0][vt1]] < 0)
		{
			if (edge_lut[vt0][vt1] < 12 ) 
			{
				//ires=vertexInterp(iso,grid[vt0],grid[vt1], pt);
				vertexInterp(iso,grid[vt0],grid[vt1], pt);
			}
			else
			{
				//ires=vertexInterpBi(iso, grid, edge_lut[vt0][vt1], vt0, pt);
				vertexInterpBi(iso, grid, edge_lut[vt0][vt1], vt0, pt);
			}
			
			
			elut[edge_lut[vt0][vt1]] = offset + npt;
			
			// add new element to vertex->face pointer list
			//vt_face_list.push_back(vt_face_list_element_type());
			
			touched[edge_lut[vt0][vt1]]++;	
			
			//npt++;
		}
		//touched[edge_lut[vt0][vt1]]++;
		
		
		return (npt+1);
	} // protected int triInterp(SbVec4f *grid, ... )
	
	

	protected int vertexInterp(final float isolevel,
			final Vec4fp p1,
			final Vec4fp p2,
			// double v[3]
			double v[] )
	{
		
		//TODO: optimize: use (void) instead of (int)
	   //double mu;
	   //Vec4fp p;

	   if (Math.abs(isolevel-p1.w ) < W_EPSILON)
	   {  
	      //v->setValue(p1[0],p1[1],p1[2]);
	      v[0] = p1.x;
	      v[1] = p1.y;
	      v[2] = p1.z;
	      return 1;
	   }
	   
	   if (Math.abs(isolevel-p2.w) < W_EPSILON)
	   {
	      //v->setValue(p2[0],p2[1],p2[2]);
	      v[0] = p2.x;
	      v[1] = p2.y;
	      v[2] = p2.z;
	      return 2;
	   }
	   
	   if (Math.abs(p1.w-p2.w) < W_EPSILON)
	   {
	      //v->setValue(p1[0],p1[1],p1[2]);
	      v[0] = p1.x;
	      v[1] = p1.y;
	      v[2] = p1.z;
	      return 1;
	   }
	   
	   double mu = (isolevel - p1.w) / (p2.w - p1.w);
	   
	   v[0] = p1.x + mu * (p2.x - p1.x);
	   v[1] = p1.y + mu * (p2.y - p1.y);
	   v[2] = p1.z + mu * (p2.z - p1.z);
	   /*
	     if ( (v[0] < 0.1) || (v[1] < 0.1) || (v[2] < 0.1) || (v[2] > 65) || (v[2] < 0.1))
	     {
	     cerr << mu << ": " << v[0] << ", " << v[1] << ", " << v[2] << endl
	     << p1[0] << ", " << p1[1] << ", " << p1[2] << ", " << p1[3] << endl
	     << p2[0] << ", " << p2[1] << ", " << p2[2] << ", " << p2[3] << endl;
	     //exit(1);
	     }
	   */
	   return 0;
	}

	protected int vertexInterpBi(
			final float isolevel,
			//SbVec4f *grid, 
			Vec4fp [] grid,
			final int edge, 
			final int startpt,
			// double v[3]
			double v[] )
	{

	   Vec4fp p1, p2, p3, p4;
	   
	   double a, b, c, d;

	   double re, im1;

	   double u, w;

	   switch (edge)
	   {
	      case 12:
	         if (startpt == 0)
	         {
	            p1 = grid[0];
	            p2 = grid[1];	
	            p3 = grid[4];
	            p4 = grid[5];
	         }
	         else
	         {
	            p1 = grid[5];
	            p2 = grid[4];	
	            p3 = grid[1];
	            p4 = grid[0];
	         }
	         break;
	      case 13:
	         if (startpt == 1)
	         {
	            p1 = grid[1];
	            p2 = grid[3];	
	            p3 = grid[5];
	            p4 = grid[7];
	         }
	         else
	         {
	            p1 = grid[7];
	            p2 = grid[5];	
	            p3 = grid[3];
	            p4 = grid[1];
	         }
	         break;
	      case 14:
	         if (startpt == 2)
	         {
	            p1 = grid[2];
	            p2 = grid[3];	
	            p3 = grid[6];
	            p4 = grid[7];	
	         }
	         else
	         {
	            p1 = grid[7];
	            p2 = grid[6];	
	            p3 = grid[3];
	            p4 = grid[2];	
	         }
	         break;
	      case 15:
	         if (startpt == 0)
	         {
	            p1 = grid[0];
	            p2 = grid[2];	
	            p3 = grid[4];
	            p4 = grid[6];
	         }
	         else
	         {
	            p1 = grid[6];
	            p2 = grid[4];	
	            p3 = grid[2];
	            p4 = grid[0];
	         }
	         break;
	      case 16:
	         if (startpt == 0)
	         {
	            p1 = grid[0];
	            p2 = grid[2];	
	            p3 = grid[1];
	            p4 = grid[3];	
	         }
	         else
	         {
	            p1 = grid[3];
	            p2 = grid[1];	
	            p3 = grid[2];
	            p4 = grid[0];	
	         }
	         break;
	      case 17:
	         if (startpt == 7)
	         {
	            p1 = grid[4];
	            p2 = grid[6];	
	            p3 = grid[5];
	            p4 = grid[7];	
	         }
	         else
	         {
	            p1 = grid[7];
	            p2 = grid[5];	
	            p3 = grid[6];
	            p4 = grid[4];	
	         }
	         break;
	      case 18:
	         if (startpt == 1)
	         {
	            p1 = grid[1];
	            p2 = grid[0];	
	            p3 = grid[5];
	            p4 = grid[4];
	         }
	         else
	         {
	            p1 = grid[4];
	            p2 = grid[5];	
	            p3 = grid[0];
	            p4 = grid[1];
	         }
	         break;
	      case 19:
	         if ( startpt == 3)
	         {
	            p1 = grid[3];
	            p2 = grid[1];	
	            p3 = grid[7];
	            p4 = grid[5];	
	         }
	         else
	         {
	            p1 = grid[5];
	            p2 = grid[7];	
	            p3 = grid[1];
	            p4 = grid[3];	
	         }
	         break;
	      case 20:
	         if (startpt == 3)
	         {
	            p1 = grid[3];
	            p2 = grid[2];	
	            p3 = grid[7];
	            p4 = grid[6];
	         }
	         else
	         {
	            p1 = grid[6];
	            p2 = grid[7];	
	            p3 = grid[2];
	            p4 = grid[3];
	         }
	         break;
	      case 21:
	         if (startpt == 2)
	         {
	            p1 = grid[2];
	            p2 = grid[0];	
	            p3 = grid[6];
	            p4 = grid[4];
	         }
	         else
	         {
	            p1 = grid[4];
	            p2 = grid[6];	
	            p3 = grid[0];
	            p4 = grid[2];
	         }
	         break;
	      case 22:
	         if (startpt == 2)
	         {
	            p1 = grid[2];
	            p2 = grid[0];	
	            p3 = grid[3];
	            p4 = grid[1];
	         }
	         else
	         {
	            p1 = grid[1];
	            p2 = grid[3];	
	            p3 = grid[0];
	            p4 = grid[2];
	         }
	         break;
	      case 23:
	         if (startpt == 5)
	         {
	            p1 = grid[5];
	            p2 = grid[7];	
	            p3 = grid[4];
	            p4 = grid[6];	
	         }
	         else
	         {
	            p1 = grid[6];
	            p2 = grid[4];	
	            p3 = grid[7];
	            p4 = grid[5];	
	         }
	         break;
	         
	      default:
	         cout( "Error: tried to interpolate wrong edge " + edge );
	      
		      p1 = new Vec4fp();
	          p2 = new Vec4fp();
	          p3 = new Vec4fp();
	          p4 = new Vec4fp();
	   }


	   if (Math.abs(isolevel-p1.w) < W_EPSILON)
	   {  
	      //v->setValue(p1[0],p1[1],p1[2]);
	      v[0] = p1.x;
	      v[1] = p1.y;
	      v[2] = p1.z;
	      return 1;
	   }

	   if (Math.abs(isolevel-p4.w ) < W_EPSILON)
	   {  
	      //v->setValue(p1[0],p1[1],p1[2]);
		   v[0] = p1.x;
		   v[1] = p1.y;
		   v[2] = p1.z;
	      return 2;
	   }

	   a = isolevel- p1.w;
	   b = isolevel- p2.w;
	   c = isolevel- p3.w;
	   d = isolevel- p4.w;

	   u =  a+d-b-c;
	   w = (-2*a)+b+c;
		

	   re = -w/(2*u);
	   im1 = (w*w)-(4*a*u);
	   im1 = Math.sqrt(im1);
	   im1 /= 2*u;

	   //cerr << re << ", " << im1 << endl;

	   if (u != 0)
	   {
	      if ( ((re+im1) >= 0.0) && ((re+im1)<=1.0) )
	         re = re+im1;
	      else if ( ((re-im1) >= 0.0) && ((re-im1)<=1.0) )
	         re = re-im1;
	      else
	      {
	         cout ( "Error: " + (re + im1) + ", " + (re-im1) + ": " 
	        		 + a + ", " + b + ", " + c + ", " + d );
	         re=0.5;
	      }
			
	   }
	   else
	   {
	      if (Math.abs(w) > W_EPSILON)
	      {
	         re=-a/w;
	      }
	      else
	      {
	    	  cout_continoue( "." );
	      }
	   }

		
	   v[0] = p1.x + re*(p4.x - p1.x);
	   v[1] = p1.y + re*(p4.y - p1.y);
	   v[2] = p1.z + re*(p4.z - p1.z);

	   return 0;

	}
	
	
	
	
//
//	public bool loadInventor(final char* fname);
//
//	public void setGlobalTransparency(double transparency) { SoMarchBase::setGlobalTransparency(transparency); };
//
//	public SbVec3f getLocal(final SbVec3f &origpt)
//    {
//        return SoMarchBase::getLocal(origpt);
//    }
//
//	public SbVec3f highlightSimplex(int index, final SbVec3f &color)
//    {
//        return SoMarchBase::highlightSimplex(index, color);
//    }
//
//	public void setupReferencePoints()
//    {
//        SoMarchBase::setupReferencePoints();
//    }
//
//	public void iterateSimplex(int iterations, 
//                                double gamma, 
//                                double alpha, 
//                                double beta, 
//                                bool regularize = false, 
//                                double kappa = 0.0, 
//                                int nbs = -1,
//                                bool docurv = true)
//    {
//        SoMarchBase::iterateSimplex(iterations, gamma, alpha, beta, 
//                                    regularize, kappa, nbs, docurv);
//    };
//
//    	
//    public static void initClass();
//
//    public void loadVolData(const char *fname, const char *origfn = NULL, 
//            bool changescale = true, /// if false the original volume extents are kept
//            int maxxsize = -1,  /// if != -1 the volume data is downsamples 
//            int maxysize = -1,  /// if != -1 the volume data is downsamples 
//            int maxzsize = -1,  /// if != -1 the volume data is downsamples 
//            bool dilate = false /// perform dilatation of volume data
//            );
//    
//    public void calculateNormals();
//    
//
//    /** basic types for marching tetrahedra */
//    protected static const int base_data_tet[16][12]; 
//    /** edge lookup table marching tetrahedra */
//    protected static const int edge_lut[8][8];
//    
//    protected static const unsigned char apex_even[5];
//    protected static const unsigned char apex_odd[5];
//    protected static const unsigned char vertices_even[5][3];
//    protected static const unsigned char vertices_odd[5][3];
//    
//    protected static const int ntet_even[5][4][4];
//    protected static const int ntet_odd[5][4][4];
//    
//    protected static const unsigned char sloc_edges[16][3];
//    
//    protected static const bool dets[16];
//    
//    /** search for a node in SoMarchBase */
//    protected SoNode* searchNode(SoSeparator *root, const SoType& type);
//    
//
//    
//    private int PolygonizeGrid(int x, int y, SbVec4f* grid, 
//                       int startpoint, float points[POINT_CACHE_SIZE][3],
//                       int startface, int faces[18*4], int &npts, bool even);
//    private int PolygoniseTri(SbVec4f *grid,double iso, int elut[24], int touches[24],
//                      int triangles[3][3], double pts[4][3], 
//                      int v0, int v1,int v2,int v3, int poffset , 
//                      int &npts, bool even);
//    private int triInterp(SbVec4f *grid, double iso, int vt0, int vt1,
//                  int elut[24], int touched[24], double pt[3], 
//                  int offset, int &npt);
//    
//    /** linear interpolation along a corner edge */
//    private int vertexInterp(double isolevel,SbVec4f p1, SbVec4f p2, double v[3]);
//    /** bi-linear interpolation along face edge */
//    private int vertexInterpBi(double isolevel, SbVec4f *grid, int edge, 
//                       int startpt, double v[3]);
//    /** tri-linear interpolation along volume diagonal */
//    private int vertexInterpTri(double isolevel, SbVec4f *grid, double v[3]);
//    
//    /** vertex index buffer */
//    int edge_0;
//    int edge_3;
//    int edge_16;
//    int edge_22;
//    
//    int edge_4;
//    int edge_8;
//    int edge_12;
//    int edge_18;
//
//    int edge_7;
//    int edge_11;
//    int edge_15;
//    int edge_21;
//
////    Separator for loading Inventor file 
////    SoSeparator* ivRoot;
    
	
	private void cout( final String msg ) 
	{
		System.out.println("SoWrapper " + msg );
	}
	
	private void cout_continoue( final String msg ) 
	{
		System.out.println("SoWrapper " + msg );
	}
};




/* ===========================================================================
   End of SoWrapper.h
   ===========================================================================
   Automatic Emacs configuration follows.
   Local Variables:
   mode:c++
   c-basic-offset: 4
   eval: (c-set-offset 'substatement-open 0)
   eval: (c-set-offset 'case-label '+)
   eval: (c-set-offset 'statement 'c-lineup-runin-statements)
   eval: (setq indent-tabs-mode nil)
   End:
   =========================================================================== */
