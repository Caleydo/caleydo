/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.jogamp.opengl.GLExtensions;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * helper class for generating a texture heat map
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapTextureRenderer {
	private final static int MAX_SAMPLES_PER_TEXTURE = 2048;
	private final static int MAX_TEMP_BUFFER_SIZE = 4 * MAX_SAMPLES_PER_TEXTURE * MAX_SAMPLES_PER_TEXTURE * 8;
	private final static int MIN_LAZY_LOADING_ITEMS = 256 * 256;

	private Dimension dimension = null;
	private final List<Tile> tiles = new ArrayList<>();

	private GLElement parent;

	/**
	 * @param tablePerspective
	 * @param blockColorer
	 */
	public HeatMapTextureRenderer() {
	}

	public void takeDown() {
		GL gl = GLContext.getCurrentGL();
		for (Tile tile : tiles) {
			tile.destroy(gl);
		}
		tiles.clear();
		dimension = null;
	}

	/**
	 * @param context
	 * @param
	 */
	public void create(IGLElementContext context, List<Integer> dimensions, List<Integer> records,
			Function2<Integer, Integer, Color> blockColorer, GLElement parent) {
		final GL gl = GLContext.getCurrentGL();
		this.parent = parent;
		final int numberOfRecords = records.size();
		final int numberOfDimensions = dimensions.size();
		dimension = new Dimension(numberOfDimensions, numberOfRecords);

		if (numberOfDimensions <= 0 || numberOfRecords <= 0) {
			// invalid data
			takeDown();
			return;
		}

		final int maxSize = resolveMaxSize(gl);

		boolean needXTiling = numberOfDimensions > maxSize;
		boolean needYTiling = numberOfRecords > maxSize;

		// pool of old texture which we might wanna reuse
		final Deque<Texture> pool = new LinkedList<>();
		for (Tile tile : tiles) {
			pool.add(tile.texture);
		}
		tiles.clear();

		final boolean doLazyLoading = numberOfDimensions * numberOfRecords > MIN_LAZY_LOADING_ITEMS;

		TileFactory f = new TileFactory(gl, pool, dimensions, records, blockColorer, doLazyLoading, this);


		if (!needXTiling && !needYTiling) {
			//single tile
			Rectangle tile = new Rectangle(0,0,numberOfDimensions,numberOfRecords);
			tiles.addAll(f.create(numberOfDimensions * numberOfRecords * 4, Collections.singletonList(tile)));
		} else if (needXTiling && !needYTiling){
			//tile in x direction only
			//fill full
			int lastTile = numberOfDimensions - maxSize;
			List<Rectangle> toload = new ArrayList<>();
			for (int i = 0; i < lastTile; i += maxSize) {
				toload.add(new Rectangle(i, 0, maxSize, numberOfRecords));
			}
			{//create rest
				int remaining = numberOfDimensions % maxSize;
				Rectangle tile = new Rectangle(numberOfDimensions-remaining,0,remaining,numberOfRecords);
				toload.add(tile);
			}
			tiles.addAll(f.create(maxSize * numberOfRecords * 4, toload));

		} else if (!needXTiling && needYTiling) {
			//tile in y direction only
			//fill full
			List<Rectangle> toload = new ArrayList<>();
			int lastTile = numberOfRecords - maxSize;
			for (int i = 0; i < lastTile; i += maxSize) {
				Rectangle tile = new Rectangle(0,i,numberOfDimensions,maxSize);
				toload.add(tile);
			}
			{//create rest
				int remaining = numberOfRecords % maxSize;
				Rectangle tile = new Rectangle(0,numberOfRecords-remaining,numberOfDimensions,remaining);
				toload.add(tile);
			}
			tiles.addAll(f.create(numberOfDimensions * maxSize * 4, toload));
		} else {
			//tile in both directions
			List<Rectangle> toload = new ArrayList<>();
			//fill full
			int lastTileR = numberOfRecords - maxSize;
			int lastTileD = numberOfDimensions - maxSize;

			for (int i = 0; i < lastTileR; i += maxSize) {
				for (int j = 0; j < lastTileD; j += maxSize) {
					Rectangle tile = new Rectangle(j,i,maxSize,maxSize);
					toload.add(tile);
				}
				{//create rest
					int remaining = numberOfDimensions % maxSize;
					Rectangle tile = new Rectangle(numberOfDimensions - remaining, i, remaining, maxSize);
					toload.add(tile);
				}
			}
			{//last line
				int iremaining = numberOfRecords % maxSize;
				int i = numberOfRecords - iremaining;
				for (int j = 0; j < lastTileD; j += maxSize) {
					Rectangle tile = new Rectangle(j, i, maxSize, iremaining);
					toload.add(tile);
				}
				{//create rest
					int remaining = numberOfDimensions % maxSize;
					Rectangle tile = new Rectangle(numberOfDimensions - remaining, i, remaining, iremaining);
					toload.add(tile);
				}
			}
			tiles.addAll(f.create(maxSize * maxSize * 4, toload));
		}

		// free remaining elements in pool
		for (Texture tex : pool)
			tex.destroy(gl);
		pool.clear();
	}



	/**
	 * compute the max texture size to use
	 *
	 * @param gl
	 * @return
	 */
	private static int resolveMaxSize(GL gl) {
		int[] result = new int[1];
		gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, result, 0);
		int maxTexSize = result[0];
		return Math.min(maxTexSize, MAX_SAMPLES_PER_TEXTURE);
	}

	protected static TextureData asTextureData(FloatBuffer buffer, int width, int height) {
		TextureData texData = new TextureData(GLProfile.getDefault(), GL.GL_RGBA /* internalFormat */, width, height,
				0 /* border */,
				GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */, false /* mipmap */,
				false /* dataIsCompressed */, false /* mustFlipVertically */, buffer, null);
		return texData;
	}

	protected static Texture toTexture(FloatBuffer buffer, Rectangle tile, Texture texture, GL gl) {
		buffer.rewind();
		TextureData texData = asTextureData(buffer, tile.width, tile.height);
		texture.updateImage(gl, texData);
		gl.glFlush();
		texData.destroy();
		return texture;
	}

	public boolean render(GLGraphics g, float w, float h) {
		if (dimension == null) // ERROR never initialized
			return false;
		float wScale = w / dimension.width;
		float hScale = h / dimension.height;

		g.save();
		//scale to be able to use pixel rendering
		g.asAdvanced().scale(wScale, hScale);

		g.gl.glEnable(GL.GL_TEXTURE_2D);
		for(Tile tile : tiles)
			tile.render(g);
		g.restore();
		return true;
	}

	private static class Tile {
		private final Rectangle tile;
		protected Texture texture;

		public Tile(Rectangle tile, Texture texture) {
			this.tile = tile;
			this.texture = texture;
		}

		/**
		 * @param gl
		 */
		public void destroy(GL gl) {
			if (this.texture != null)
				texture.destroy(gl);
		}

		public void render(GLGraphics g) {
			if (this.texture == null) { // not yet loaded
				g.gl.glDisable(GL.GL_TEXTURE_2D);
				// FIXME find something better
				g.color(Color.YELLOW).fillRect(tile.x, tile.y, tile.width, tile.height);
				g.gl.glEnable(GL.GL_TEXTURE_2D);
				return;
			}

			texture.bind(g.gl);
			final int clamp = (g.gl.isExtensionAvailable(GLExtensions.VERSION_1_2) || !g.gl.isGL2()) ? GL.GL_CLAMP_TO_EDGE
					: GL2.GL_CLAMP;
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, clamp);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, clamp);

			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

			// flipped in y-direction
			g.fillImage(texture, tile.x, tile.y + tile.height, tile.width, -tile.height);
		}

		/**
		 * @return the tile, see {@link #tile}
		 */
		public Rectangle getTile() {
			return tile;
		}
	}

	@ListenTo(sendToMe = true)
	private void onTileLoadedEvent(TileLoadedEvent event) {
		GL gl = GLContext.getCurrentGL();
		Rectangle tile = event.getTile();
		FloatBuffer buffer = event.getBuffer();
		Tile t = byTile(tile);
		if (t == null)
			return;
		if (!(t instanceof LazyTile))
			return;
		Texture texture = toTexture(buffer, tile, TextureIO.newTexture(GL.GL_TEXTURE_2D), gl);
		((LazyTile) t).loaded(texture);
		if (parent != null)
			parent.repaint();

		TileLoaderJob next = event.getNext();
		if (next != null) { // schedule next in the chain
			t = byTile(next.getTile());
			if (t instanceof LazyTile) {
				((LazyTile) t).setJob(next);
				next.schedule();
			}
		}
	}

	private Tile byTile(Rectangle tile) {
		for (Tile t : tiles) {
			if (Objects.equals(tile, t.getTile()))
				return t;
		}
		return null;
	}

	private static class LazyTile extends Tile {
		private Job job;

		public LazyTile(Rectangle tile, Job job) {
			super(tile, null);
			this.job = job;
		}

		/**
		 * @param next
		 */
		public void setJob(Job job) {
			this.job = job;
		}

		public void loaded(Texture texture) {
			this.texture = texture;
			this.job = null;
		}

		@Override
		public void destroy(GL gl) {
			super.destroy(gl);
			if (this.job != null) {
				this.job.cancel();
			}
		}

	}

	private static class TileLoader {
		private final List<Integer> dimensions;
		private final List<Integer> records;
		private final Function2<Integer, Integer, Color> blockColorer;

		public TileLoader(List<Integer> dimensions, List<Integer> records,
				Function2<Integer, Integer, Color> blockColorer) {
			this.dimensions = dimensions;
			this.records = records;
			this.blockColorer = blockColorer;
		}

		protected void load(FloatBuffer buffer, Rectangle tile, IProgressMonitor monitor) {
			final int ilast = tile.y + tile.height;
			final int jlast = tile.x + tile.width;

			// fill buffer
			buffer.rewind();
			for (int i = tile.y; i < ilast; ++i) {
				if (monitor.isCanceled())
					return;
				int recordID = records.get(i);
				for (int j = tile.x; j < jlast; ++j) {
					int dimensionID = dimensions.get(j);
					Color color = blockColorer.apply(recordID, dimensionID);
					buffer.put(color.getRGBA());
				}
			}
		}
	}

	private static class TileFactory extends TileLoader {
		private final GL gl;
		private final Deque<Texture> pool;
		private final Object receiver;
		private final boolean doLazyLoading;

		public TileFactory(GL gl, Deque<Texture> pool, List<Integer> dimensions, List<Integer> records,
				Function2<Integer, Integer, Color> blockColorer, boolean doLazyLoading, Object receiver) {
			super(dimensions, records, blockColorer);
			this.gl = gl;
			this.pool = pool;
			this.doLazyLoading = doLazyLoading;
			this.receiver = receiver;
		}

		/**
		 * @param i
		 * @param singleton
		 * @return
		 */
		public Collection<Tile> create(int bufferSize, List<Rectangle> tiles) {
			Collection<Tile> r = new ArrayList<>(tiles.size());
			if (!doLazyLoading) {
				FloatBuffer buffer = FloatBuffer.allocate(bufferSize); // w*h*rgba
				for (Rectangle tile : tiles)
					r.add(createNow(buffer, tile));
			} else {
				int groupCount = Math.max(bufferSize * tiles.size() / MAX_TEMP_BUFFER_SIZE, 3);
				// System.out.println("tile loading group count: " + groupCount);
				for (int i = 0; i < tiles.size(); i += groupCount) {
					int last = Math.min(i + groupCount, tiles.size());
					Deque<Rectangle> sub = new ArrayDeque<>(tiles.subList(i, last));
					TileLoaderJob job = new TileLoaderJob(receiver, this, bufferSize, sub);
					for (Rectangle tile : tiles.subList(i, last)) {
						r.add(new LazyTile(tile, job));
					}
					job.schedule();
				}
			}

			return r;
		}

		private Tile createNow(FloatBuffer buffer, Rectangle tile) {
			load(buffer, tile, new NullProgressMonitor());

			Texture texture;
			if (!pool.isEmpty())
				texture = pool.poll();
			else
				texture = TextureIO.newTexture(GL.GL_TEXTURE_2D);
			texture = toTexture(buffer, tile, texture, gl);

			return new Tile(tile, texture);
		}
	}

	// public static void main(String[] args) {
	// MockDataDomain d = MockDataDomain.createNumerical(100, 50, new Random());
	// GLSandBox.main(args, new HeatMapElement(d.getDefaultTablePerspective(), BasicBlockColorer.INSTANCE,
	// EDetailLevel.LOW));
	// }

	private static class TileLoaderJob extends Job {
		private final Object receiver;
		private final TileLoader loader;
		private final Rectangle tile;
		private final Supplier<FloatBuffer> buffer;
		private final Deque<Rectangle> tileQueue;

		/**
		 * @param name
		 */
		public TileLoaderJob(Object receiver, TileLoader loader, Supplier<FloatBuffer> buffer,
				Deque<Rectangle> tileQueue) {
			super("Tile Loader: " + tileQueue.getFirst());
			this.receiver = receiver;
			this.loader = loader;
			this.buffer = buffer;
			this.tile = tileQueue.pollFirst();
			this.tileQueue = tileQueue;
		}

		public TileLoaderJob(Object receiver, TileLoader loader, final int bufferSize, Deque<Rectangle> tileQueue) {
			this(receiver, loader, Suppliers.memoize(new Supplier<FloatBuffer>() {
				@Override
				public FloatBuffer get() {
					return FloatBuffer.allocate(bufferSize);
				}
			}), tileQueue);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// Stopwatch w = new Stopwatch().start();
			FloatBuffer b = buffer.get();
			loader.load(b, tile, monitor);
			// System.out.println("done in " + w);
			if (!monitor.isCanceled()) {
				TileLoaderJob next = null;
				if (!tileQueue.isEmpty()) {
					next = new TileLoaderJob(receiver, loader, Suppliers.ofInstance(b), tileQueue);
				}
				EventPublisher.trigger(new TileLoadedEvent(tile, b, next).to(receiver));
			}
			return Status.OK_STATUS;
		}

		/**
		 * @return the tile, see {@link #tile}
		 */
		public Rectangle getTile() {
			return tile;
		}
	}

	private static final class TileLoadedEvent extends ADirectedEvent {

		private final Rectangle tile;
		private final FloatBuffer buffer;
		private final TileLoaderJob next;

		/**
		 * @param tile
		 * @param buffer
		 */
		public TileLoadedEvent(Rectangle tile, FloatBuffer buffer, TileLoaderJob next) {
			this.tile = tile;
			this.buffer = buffer;
			this.next = next;
		}

		public TileLoaderJob getNext() {
			return next;
		}

		/**
		 * @return the tile, see {@link #tile}
		 */
		public Rectangle getTile() {
			return tile;
		}

		/**
		 * @return the buffer, see {@link #buffer}
		 */
		public FloatBuffer getBuffer() {
			return buffer;
		}

	}
}
