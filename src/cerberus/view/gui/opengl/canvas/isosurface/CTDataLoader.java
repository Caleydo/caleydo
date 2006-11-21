package cerberus.view.gui.opengl.canvas.isosurface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//import cerberus.view.gui.opengl.canvas.isosurface.GeneralByteReader;
import cerberus.view.gui.opengl.canvas.isosurface.SoWrapper;

public class CTDataLoader {

	protected File m_sourceFile = null;
	
	protected short [] dataArray = null;
	
	protected int [] dimensions = null;
	
	private static final int iSizeDimension = 4;
	
	private static final int iHeaderSizeInByte = 348;
	
	public CTDataLoader() {
		dimensions = new int[iSizeDimension];
	}
	
	public static void main(String[] args) {
		
		String inputfile = "..\\..\\data\\CTdata\\p213";
		
		if ( args.length < 1 ) 
		{
			System.err.println(" Need datefile name as argument!");
		}
		else 
		{
			inputfile = args[0];
		}
		
		CTDataLoader loader = new CTDataLoader();
		
		//loader.readHeader( inputfile + ".hdr" );
		
		//int [] iDim = {512,512,80,1};
		int [] iDim = {32,32,32,1};
		
		loader.setDimensions( iDim );
		loader.createDataSet();		
		//loader.createEmptyDataSet();
		
		loader.setFileName( inputfile + ".img" );		
		//loader.readDataSet();
		
		SoWrapper isosurface = new SoWrapper();
		
		isosurface.setDimensions( iDim ) ;
		isosurface.assignData( loader.getDataArray() );
		
		isosurface.march( 26 );
		
	}
	
	public void setFileName( final String fileName ) {
		this.m_sourceFile = new File( fileName );
	}
	
	public void setDimensions( final int [] dimensionsArray ) {
		
		if ( dimensionsArray.length < iSizeDimension ) 
		{
			throw new RuntimeException("setDimensions() needs at least 4 dimensions!");
		}
		
		for ( int i=0; i < iSizeDimension; i++ )
		{
			dimensions[i] = dimensionsArray[i];
		}
	}
	
	public synchronized int [] getDimensions() {
		
		int [] dimensionsResult = new int [iSizeDimension];
		
		for ( int i = 0; i < iSizeDimension; i++)
		{
			dimensionsResult[i] = dimensions[i];
		}
		
		return dimensionsResult;
	}
	
	public void readHeader( String headerFileName ) 
	{
		try
		{
			BufferedInputStream bisteam = new BufferedInputStream(
					new FileInputStream( headerFileName ));
			
			byte[] headerBuffer = new byte[ iHeaderSizeInByte ];
			
			try 
			{
				try 
				{
					int iSizeHeader = bisteam.available();
					
					if ( iSizeHeader != iHeaderSizeInByte ) 
					{
						System.err.println("readHeader(" + headerFileName + ") Header=" + iSizeHeader + 
								" is not of excpected size!");
					}
				}
				catch ( IOException ioe) 
				{
					System.err.println("readHeader(" + headerFileName + ") error while reading avalable Header");
					return;
				}
			
				bisteam.read( headerBuffer );
				
				
				for ( int i=0; i < 100 ; i++ )
				{
					Byte test = new Byte( headerBuffer[i] );
					System.out.println(" " + i + "# s=" +
							test.shortValue() + 
							" s(-1)=" +
							Short.reverseBytes( test.shortValue() ) +
							"  i=" +
							test.intValue() + " [" +
							test.toString() + "]");
				}
				
				int testN= -1;
				//int iResult = GeneralByteReader.toByteArrayLSB(80,headerBuffer,testN);
				
				
				short iReadDimensionDatabase = (short) headerBuffer[40];
				
				if ( iSizeDimension != iReadDimensionDatabase ) 
				{
					System.err.println("readHeader(" + headerFileName + 
							") size of dimension=[" + 
							iReadDimensionDatabase + "] inside header do not match 4 as excpected!");
					return;
				}
				
				short [] sDimensionArray = new short[ iSizeDimension ];
				int [] iDimensionArray = new int[ iSizeDimension ];
				
				for ( int i=0; i < iSizeDimension;i++ )
				{
					sDimensionArray[i] = (short) headerBuffer[40+i];
					iDimensionArray[i] = (int) sDimensionArray[i];
					
					System.out.println( "  header dim[" + i + "] = " + sDimensionArray[i] );
				}
				
				setDimensions( iDimensionArray );
			
			}
			catch (IOException ioe2) 
			{
				System.err.println("readHeader(" + headerFileName + ") error while reading Header");
				
			}
			finally {
				
				if ( bisteam != null ) {
					try 
					{
						bisteam.close();
					}
					catch (IOException ioe2) 
					{ }
				}
				
			}
			
		}
		catch ( FileNotFoundException fnfe ) 
		{
			System.err.println("readHeader(" + headerFileName + ") file not found!");
			
		}
	}
	
	public void createDataSet() {
		
		float fxCenter =  (float) dimensions[0] / 2.0f;
		float fyCenter =  (float) dimensions[1] / 2.0f;
		float fzCenter =  (float) dimensions[2] / 2.0f;
		
		int xCenter =  (int) fxCenter;
		int yCenter =  (int) fyCenter;
		int zCenter =  (int) fzCenter;
		
		float intensityScale = 0.5f;
		float intensityOffset = 1.0f;
		
		float maxValue = intensityScale * (float) Math.sqrt( fxCenter*fxCenter + fyCenter*fyCenter + fzCenter*fzCenter) + intensityOffset;
		
		dataArray = new short [ dimensions[0] * dimensions[1] * dimensions[2] ];
		
		int iIndex = 0;
		
		float a = 0,b = 0,c = 0;
		
		for ( int k = 0; k < dimensions[3] ; k++ ) 
		{
			a = Math.abs( zCenter - k);
			
			
			for ( int j=0; j < dimensions[2]; j++ )
			{
				b = Math.abs( yCenter - j);
				
				
				for ( int i=0; i < dimensions[1]; i++ )
				{
					c = Math.abs( xCenter - i);
					
					float value = maxValue + intensityScale * (float) Math.sqrt( a*a + b*b + c*c);
					
					dataArray[iIndex] =  (short) value;
					iIndex++;	
				}
			}
		}
	}
	
	public void createEmptyDataSet() {
		
		short isoValue = 0;
		
		dataArray = new short [ dimensions[0] * dimensions[1] * dimensions[2] ];
		
		int iIndex = 0;
		
		float a = 0,b = 0,c = 0;
		
		for ( int k = 0; k < dataArray.length ; k++ ) 
		{
			dataArray[k] =  isoValue;
		}
		
		dataArray[16*32*32 + 16*32 + 16] = 250;
	}
	
	public short [] getDataArray() 
	{
		return dataArray;
	}
	
	public void readDataSet() {
		
		if ( m_sourceFile == null ) 
		{
			System.err.println("not data file is set!");
			return;
		}
		
		FileInputStream fistream = null;
		BufferedInputStream in = null;
		
		try 
		{
			fistream = new FileInputStream( m_sourceFile );
			
			in = new BufferedInputStream(fistream);
			
			int iReadSucessiveBytes = 0;
			
			
			try {
				iReadSucessiveBytes = in.available();
			}
			catch ( IOException ioe) 
			{
				System.err.println("IO error while reading file!");
				
				try {
					in.close();
				}
				catch ( IOException ioe2) 
				{
					in = null;
				}
				
				return;
			}
			
			System.err.println("file opened ...");
			
			/**
			 * Create data array
			 */
			if ( dataArray != null ) 
			{
				if ( dataArray.length != iReadSucessiveBytes )
				{
					dataArray = new short[ iReadSucessiveBytes ];
				}
			}
			
			byte [] buffer = new byte[ iReadSucessiveBytes ]; 
			
			try 
			{
				in.read( buffer );
				
				for ( int i=0; i<iReadSucessiveBytes; i++)
				{
					dataArray[i] = (short) buffer[i];
				}
			}
			catch ( IOException ioe) 
			{
				System.err.println("IO error while reading file!");
				
				buffer = null;
			}
			
			try {
				in.close();
			}
			catch ( IOException ioe2) 
			{
				in = null;
			}
		}
		catch ( FileNotFoundException fnfe)
		{
			System.err.println("data file was not found!" + m_sourceFile.getName() );
		}
		
	}

}
