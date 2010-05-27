/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * Created on 2006-okt-30
 */
package com.jmex.subdivision;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * An abstract class for subdivision of surfaces.<br>
 * Implementing classes must implement <code>prepare()</code> and <code>doSubdivide()</code> and preferrably override <code>computeNormals(TriMesh batch)</code><br>
 * <br> 
 * Usage of subclass <code>SubdivisionButterfly</code>:<br><br>
 * <code>
 * TriMesh mesh = {some trimesh};<br>
 * Subdivision subdivision = new SubdivisionButterfly(mesh.getBatch(0)); // prepare for subdivision<br>
 * subdivision.subdivide(); // subdivide<br>
 * subdivision.apply(); // Applies the new subdivided buffers to the batch<br>
 * subdivision.computeNormals(); // calculate new normals <br>
 * </code><br>
 * <br>
 * Or you can use it without giving it a batch:<br>
 * <br>
 * <code>
 * Subdivision subdivision = new SubdivisionButterfly();<br>
 * subdivision.setVertexBuffer(batch.getVertexBuffer());<br>
 * subdivision.setIndexBuffer(batch.getIndexBuffer());<br>
 * subdivision.addToBufferList(batch.getTextureBuffer(0), Subdivision.BufferType.TEXTUREBUFFER);<br>
 * subdivision.addToBufferList(batch.getTextureBuffer(1), Subdivision.BufferType.TEXTUREBUFFER);<br>
 * subdivision.addToBufferList(batch.getColorBuffer(), Subdivision.BufferType.COLORBUFFER);<br>
 * subdivision.subdivide(); // subdivide<br>
 * subdivision.apply(mesh.getBatch(0)); // Applies the new subdivided buffers to the batch<br> 
 * subdivision.computeNormals(mesh.getBatch(0)); // calculate new normals<br> 
 * </code>
 * 
 * 
 * @author Tobias (tobbe.a removethisoryourclientgoesape gmail.com)
 */
public abstract class Subdivision {
    private static final Logger logger = Logger.getLogger(Subdivision.class.getName());
	
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer newVertexBuffer;
	protected IntBuffer indexBuffer;
	protected IntBuffer newIndexBuffer;
	protected ArrayList<SubdivisionBuffer> buffers;
	protected ArrayList<SubdivisionBuffer> newBuffers;
	private TriMesh mesh;
	private boolean prepared;
	
	/**
	 * Constructor for Subdivision
	 *
	 */
	public Subdivision() {
		buffers = new ArrayList<SubdivisionBuffer>();
		newBuffers = new ArrayList<SubdivisionBuffer>();
		mesh = null;
		vertexBuffer = null;
		indexBuffer = null;
		newVertexBuffer = null;
		newIndexBuffer = null;
		prepared = false;
	}
		
	/**
	 * Constructor for Subdivision
	 * 
	 * @param vertexBuffer The vertex buffer to use when subdividing. Specify other buffers (normals,colors,texcoords) with <code>addToBufferList</code>
	 * @param indexBuffer The index buffer to use when subdividing
	 */
	public Subdivision(FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
		this();
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
	}

	/**
	 * Constructor for Subdivision
	 * 
	 * @param batch The TriMesh that are to be subdivided
	 */
	public Subdivision(TriMesh batch) {
		this();
		setBatch(batch);
	}
	
	/**
	 * Whichever preparations are needed are to be performed here
	 * 
	 * @return <code>true</code> if the preparation succeeded
	 */
	public abstract boolean prepare();

	/**
	 * The actual subdivision, places the new buffers in the members newVertexBuffer, newIndexBuffer, newBuffers
	 * 
	 * @return <code>true</code> if the subdivision succeeded
	 */
	protected abstract boolean doSubdivide();
	
	/**
	 * Sets a batch that this Subdivision should subdivide. Adds color buffer and texture buffers to the Subdivision if they are present in the batch.
	 * You can also add buffers manually with <code>addToBufferList(FloatBuffer buffer, int elemSize, boolean linear)</code> if you don't use this method.
	 * 
	 * Removes all other buffers. Replaces a previous batch.
	 * 
	 * Normals are not interpolated by default, since it gives poor results, call computeNormals() after subdividing instead. However, to interpolate the normals as well, just call addToBufferList() with the normal buffer after calling this method. 
	 * 
	 * @param batch The batch to set.
	 */
	public void setBatch(TriMesh batch) {
		this.mesh = batch;
		clearBufferList();
		setVertexBuffer(batch.getVertexBuffer());
		setIndexBuffer(batch.getIndexBuffer());
		
		// Add the batch's color buffer
		if (batch.getColorBuffer() != null)
			addToBufferList(batch.getColorBuffer(), BufferType.COLORBUFFER); // Color buffer have elements of size 4 (floats)
		
		// Add the batch's texture buffers
		ArrayList<TexCoords> texBufs = batch.getTextureCoords();
		TexCoords texBuf;
		for (Iterator<TexCoords> it = texBufs.iterator(); it.hasNext(); ) {
			texBuf = it.next();
			if (texBuf != null)
				addToBufferList(texBuf.coords, BufferType.TEXTUREBUFFER);  // Texture buffers have elements of size 2 (floats) and should be interpolated linearly
		}
	}
	
	/**
	 * Performs the subdivision after checking that everything is ok
	 * 
	 * @return <code>true</code> if the subdivision succeeded
	 */
	public boolean subdivide() {
		if (getVertexBuffer()==null) {
			logger.warning("No vertex buffer is set, aborting.");
			return false;
		}
		if (getIndexBuffer()==null) {
			logger.warning("No index buffer is set, aborting.");
			return false;
		}
		if (!isValid()) return false;
		if (!prepared) 
			if (!(prepared = prepare())) {
				logger.warning("Could not prepare for subdivision, aborting.");
				return false;
			}
		if (doSubdivide()) {
			if (newVertexBuffer != null || newIndexBuffer != null) {
				setVertexBuffer(newVertexBuffer);
				newVertexBuffer = null;
				setIndexBuffer(newIndexBuffer);
				newIndexBuffer = null;
				buffers = newBuffers;
				newBuffers = null;
				return true;
			} else return false;
			
		} else return false;
	}
	
	/**
	 * Applies the buffers to the set batch
	 * 
	 * @return <code>true</code> if applied
	 */
	public boolean apply() {
		return apply(mesh);
	}
	
	/**
	 * Applies the buffers to the given batch
	 * 
	 * @param batch The batch to apply the buffers to
	 * @return <code>true</code> if applied
	 */
	public boolean apply(TriMesh batch) {
		if (batch == null) {
			logger.warning("No batch is set to apply the buffers to, aborting.");
			return false;
		}
		if (vertexBuffer==null || indexBuffer==null) {
			logger.warning("No vertex or index buffer is set, aborting.");
			return false;
		}
		if (!isValid()) return false;
		
		// Everything is ok, apply the buffers to the batch
		int texBufIndex = 0;
		SubdivisionBuffer subBuf = null;
		batch.setVertexBuffer(vertexBuffer);
		batch.setIndexBuffer(indexBuffer);
		
		for (Iterator<SubdivisionBuffer> it = buffers.iterator(); it.hasNext(); ) {
			subBuf = it.next();
			if (subBuf != null) {
				switch (subBuf.type) {
				case COLORBUFFER:
					batch.setColorBuffer(subBuf.buf);
					break;
				case TEXTUREBUFFER:
					batch.setTextureCoords(new TexCoords(subBuf.buf), texBufIndex); // Could this place the texture buffers at the wrong index? If the iterator doesn't iterate in the order the elements were inserted perhaps? 
					texBufIndex++;
					break;
				case NORMALBUFFER:
					batch.setNormalBuffer(subBuf.buf);
					break;
				default:
					logger.warning("Unknown buffer type, not applying it.");
				}
			}
		}
		return true;		
	}
	
	/**
	 * Computes the normals for the set batch
	 *
	 */
	public void computeNormals() {
		computeNormals(mesh);
	}
	
	/**
	 * Computes normals for the given batch. Taken from 
	 * com.jmex.model.XMLparser.Converters.AseToJme.java (and optimized).
	 * Should be overridden since there are methods for each subdivision scheme
	 * to compute new normals, which give better results.
	 * 
	 * @param batch
	 */
	public void computeNormals(TriMesh batch) {
		Vector3f vector1 = new Vector3f();
		Vector3f vector2 = new Vector3f();
		Vector3f vector3 = new Vector3f();
		
		FloatBuffer vb = batch.getVertexBuffer();
		IntBuffer ib = batch.getIndexBuffer();
		int tCount = batch.getTriangleCount();
		int vCount = batch.getVertexCount();
		
		// Get the current object
		// Here we allocate all the memory we need to calculate the normals
		Vector3f[] tempNormals = new Vector3f[tCount];
		Vector3f[] normals = new Vector3f[vCount];
		
		// Go through all of the faces of this object
		for (int i = 0; i < tCount; i++) {
			BufferUtils.populateFromBuffer(vector1, vb, ib.get(i*3));
			BufferUtils.populateFromBuffer(vector2, vb, ib.get(i*3+1));
			BufferUtils.populateFromBuffer(vector3, vb, ib.get(i*3+2));
			vector1.subtractLocal(vector3);
			tempNormals[i] = vector1.cross(vector3.subtract(vector2)).normalizeLocal();
		}
		
		Vector3f sum = new Vector3f();
		int shared = 0;
		
		for (int i = 0; i < vCount; i++) {
			for (int j = 0; j < tCount; j++) {
				if (ib.get(j*3) == i
						|| ib.get(j*3+1) == i
						|| ib.get(j*3+2) == i) {
					sum.addLocal(tempNormals[j]);
					
					shared++;
				}
			}
			normals[i] = sum.divide((-shared)).normalizeLocal();
			
            sum.zero(); // Reset the sum
            shared = 0; // Reset the shared
		}
		batch.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
	}
	
	/**
	 * Unsets the batch. Does not clear the buffer lists.
	 *
	 */
	public void unsetBatch() {
		this.mesh = null;
	}

	/**
	 * Add a buffer to the list of buffers who are to be subdivided
	 * 
	 * @param buffer The buffer to be added
	 * @param elemSize The size of the elements in the buffer (e.g. 3 for normal buffers, 2 for texture buffers, 4 for color buffers)
	 * @param linear Whether or not the buffer should be linearly interpolated (for example, it doesn't make sense to use higher-order interpolation on texture coordinates). Should probably be <code>false</code> in most cases
	 * @param type The type of buffer, needed when applying buffers back to the batch
	 */
	public void addToBufferList(FloatBuffer buffer, int elemSize, boolean linear, BufferType type) {
		buffers.add(new SubdivisionBuffer(buffer, elemSize, linear, type));
	}
	
	/**
	 * Add a buffer to the list of buffers who are to be subdivided
	 * 
	 * @param buffer The buffer to be added
	 * @param type The type of buffer, needed when applying buffers back to the batch
	 */
	public void addToBufferList(FloatBuffer buffer, BufferType type) {
		buffers.add(new SubdivisionBuffer(buffer, type));
	}
	
	/** 
	 * Removes a buffer from the bufferlist
	 * 
	 * @param buffer The buffer to remove from the list of buffers to be subdivided
	 */
	public void removeBuffer(FloatBuffer buffer) {
		SubdivisionBuffer removeBuf = null;
		boolean found = false;
		for (Iterator<SubdivisionBuffer> it = buffers.iterator(); it.hasNext() && (!found); ) {
			removeBuf = it.next();
			if (removeBuf.buf == buffer) found = true;
		}
		buffers.remove(removeBuf);		
	}
	
	/**
	 * Clear the list of buffers to be interpolated
	 *
	 */
	public void clearBufferList() {
		buffers = new ArrayList<SubdivisionBuffer>();
	}
	
	/**
	 * Checks whether the buffers submitted to this Subdivision are valid (i.e. if they have as many elements as there are vertices in the vertexbuffer)
	 * @return <code>true</code> if valid
	 */
	public boolean isValid() {
		boolean valid = true;
		String errors = "";
		SubdivisionBuffer subBuf;
		
		int vertexCount = vertexBuffer.capacity() / 3;
		for (int i = 0; i<buffers.size(); i++) {
			subBuf = buffers.get(i);
			if (subBuf != null) 
				if ((subBuf.buf.capacity() / subBuf.elemSize) != vertexCount) {
					// The buffer does not have as many elements as there are vertices
					valid = false;
					errors = errors + "SubdivisionBuffer at index " + i + " does not have as many elements as there are vertices in the vertex buffer.\n";
				}
		}
		if (!valid) {
            logger.warning(errors);
        }
		return valid;
	}
	
	/**
	 * @return Returns the batch.
	 */
	public TriMesh getBatch() {
		return mesh;
	}
	/**
	 * @return Returns the indexBuffer.
	 */
	public IntBuffer getIndexBuffer() {
		return indexBuffer;
	}
	/**
	 * @param indexBuffer The indexBuffer to set.
	 */
	public void setIndexBuffer(IntBuffer indexBuffer) {
		this.indexBuffer = indexBuffer;
		prepared = false;
	}
	/**
	 * @return Returns the vertexBuffer.
	 */
	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	/**
	 * @param vertexBuffer The vertexBuffer to set.
	 */
	public void setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
		prepared = false;
	}
	/**
	 * Get the vertex count of the set vertex buffer (capacity / 3)
	 * 
	 * @return The vertex count
	 */
	public int getVertexCount() {
		if (vertexBuffer == null) {
			logger.warning("No vertex buffer set, aborting.");
			return 0;
		}
		return vertexBuffer.capacity() / 3;
	}
	/**
	 * Buffer types. Needed to know where to apply buffers to the batch. 
	 * @author Tobias
	 */
	public enum BufferType {COLORBUFFER,TEXTUREBUFFER,NORMALBUFFER,OTHER}

	protected class SubdivisionBuffer {
		/**
		 * The buffer
		 */
		public FloatBuffer buf;
		/**
		 * The buffer's element size (e.g. 3 for normal buffers, 2 for texture buffers, 4 for color buffers)
		 */
		public int elemSize;
		/**
		 * Whether or not the buffer should be linearly interpolated
		 * (for example, it doesn't make sense to use higher-order
		 * interpolation on texture coordinates, should probably be 
		 * <code>false</code> in most cases)
		 */
		public boolean linear;
		
		/**
		 * states which type of buffer this is
		 */
		public BufferType type;
		
		/**
		 * Constructor for SubdivisionBuffer
		 * @param buf The buffer
		 * @param elemSize The buffer's element size (e.g. 3 for normal buffers, 2 for texture buffers, 4 for color buffers)
		 * @param linear Whether or not the buffer should be linearly interpolated (for example, it doesn't make sense to use higher-order interpolation on texture coordinates). Should probably be <code>false</code> in most cases
		 * @param type The type of buffer, needed when applying buffers to batches
		 */
		public SubdivisionBuffer(FloatBuffer buf, int elemSize, boolean linear, BufferType type) {
			this.buf = buf;
			this.elemSize = elemSize;
			this.linear = linear;
			this.type = type;
		}

		/**
		 * Constructor for SubdivisionBuffer
		 * @param buf The buffer
		 * @param type The type of buffer, needed when applying buffers to batches
		 */
		public SubdivisionBuffer(FloatBuffer buf, BufferType type) {
			this.buf = buf;
			switch (type) {
			case COLORBUFFER:
				this.elemSize = 4;
				this.linear = false;
				break;
			case TEXTUREBUFFER:
				this.elemSize = 2;
				this.linear = true;
				break;
			case NORMALBUFFER:
				this.elemSize = 3;
				this.linear = false;
				break;
			default:
				logger.warning("Unknown buffer type, guessing its elemSize and linearity");
				this.elemSize = 3;
				this.linear = false;
				break;
			}
			this.type = type;
		}
	}
}
