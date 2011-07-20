package org.caleydo.view.heatmap.uncertainty;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class VisualUncertaintyUtil {

	public static void calcVisualUncertainty(GL2 gl, PixelGLConverter pixelGLConverter,
			ElementLayout layout, HeatMapTextureRenderer renderer, ArrayList<Float> ret) {

		// float sizeX = clusterHeatMapLayout.getUnscalableElementWidth();
		float scaledX = layout.getSizeScaledX();
		float scaledY = layout.getSizeScaledY();

		// float transX = layout.getTranslateX();
		float transY = layout.getTranslateY();

		int width = pixelGLConverter.getPixelWidthForGLWidth(scaledX);
		// int xScreen = pixelGLConverter.getPixelWidthForGLWidth(transX);

		int height = pixelGLConverter.getPixelWidthForGLWidth(scaledY);
		// int yScreen = pixelGLConverter.getPixelWidthForGLWidth(transY);

		for (int i = 0; i < height; i++) {
			ret.add(renderer.getUncertaintyForLine(i, width, height));
		}
		// getScreenAreaShot(gl, xScreen, yScreen, width, height);
		// HeatMapRenderStyle renderStyle = uncertaintyHeatMap.getRenderStyle();
		// float x = renderStyle.getXScaling();
		// float y = renderStyle.getYScaling();
	}

	private static void getScreenAreaShot(GL2 gl, int x, int y, int width, int height) {

		/*
		 * if (myFBO == -1) { myFBO = genFBO(gl);}
		 * gl.glBindFramebuffer(GL2.GL_DRAW_BUFFER, this.myFBO);
		 * gl.glReadBuffer(GL2.GL_FRONT); gl.glDrawBuffer(myFBO);
		 * 
		 * gl.glBlitFramebuffer(x, y, x + width - 1, y + height - 1, 0, 0, 600,
		 * 600, GL2.GL_COLOR_BUFFER_BIT, GL2.GL_LINEAR);
		 * //gl.glBlitFramebuffer(x, y, x+width, y+height, 0, 0,
		 * numberOfExpirments, numberOfElements, GL2.GL_COLOR_BUFFER_BIT, //
		 * GL2.GL_LINEAR);
		 */
		// gl.glReadBuffer(GL2.GL_AUX1);
		// gl.glDrawBuffer(GL2.GL_BACK);

		gl.glReadBuffer(GL2.GL_FRONT);
		ByteBuffer screenShotByteBuffer = null;
		BufferedImage screenShotImage = null;
		screenShotImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		screenShotByteBuffer = ByteBuffer.wrap(((DataBufferByte) screenShotImage
				.getRaster().getDataBuffer()).getData());

		gl.glReadPixels(x, y, width, height, GL2.GL_ABGR_EXT, GL2.GL_UNSIGNED_BYTE,
				screenShotByteBuffer);

		ImageUtil.flipImageVertically(screenShotImage);

		Date now = new Date();

		try {
			ImageIO.write(screenShotImage, "png", new File(
					"C:\\Documents and Settings\\Clemens\\bild" + now.getTime() + ".png"));
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		// resizeImageByFactor();

		// float xScale = width / numberOfExpirments;
		// float yScale = height / numberOfElements;

		// Fgl.glBindFramebuffer(GL2.GL_DRAW_BUFFER, 0);
		// gl.glReadBuffer(GL2.GL_NONE);
		// gl.glDrawBuffer(GL2.GL_BACK);

	}

	public float getValueFromBytes(byte[] abgr) {

		float val = -((abgr[2] + 128) / 255f) + ((abgr[3] + 128) / 255f);
		return val;

	}

	public void getScreenAreaShot2(GL2 gl, int x, int y, int width, int height) {

		// readScreenShot
		{
			ByteBuffer screenShotByteBuffer = null;
			BufferedImage screenShotImage = null;
			screenShotImage = new BufferedImage(width, height,
					BufferedImage.TYPE_4BYTE_ABGR);

			screenShotByteBuffer = ByteBuffer.wrap(((DataBufferByte) screenShotImage
					.getRaster().getDataBuffer()).getData());

			gl.glReadBuffer(GL2.GL_FRONT);
			gl.glReadPixels(x, y, width, height, GL2.GL_ABGR_EXT, GL2.GL_UNSIGNED_BYTE,
					screenShotByteBuffer);

			// create new Texture from ScreenShot
			TextureData texData = new TextureData(GLProfile.getDefault(),
					GL2.GL_RGBA /* internalFormat */, height /* height */,
					width /* width */, 0 /* border */, GL2.GL_RGBA /* pixelFormat */,
					GL2.GL_UNSIGNED_BYTE /* pixelType */, false /* mipmap */,
					false /* dataIsCompressed */, false /* mustFlipVertically */,
					screenShotByteBuffer, null);
			Texture tex = TextureIO.newTexture(0);
			tex.updateImage(texData);

			try {
				ImageIO.write(screenShotImage, "png", new File(
						"C:\\Documents and Settings\\Clemens\\bild.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * // Draw ScreenShot as new Texture in BackBuffer
		 * gl.glDrawBuffer(GL2.GL_BACK); // gl.glDrawBuffer(GL2.GL_FRONT);
		 * PixelGLConverter pixelGLConverter = this.uncertaintyHeatMap
		 * .getPixelGLConverter(); float glHeight =
		 * pixelGLConverter .getGLHeightForPixelHeight(numberOfElements); float
		 * glWidth = pixelGLConverter
		 * .getGLWidthForPixelWidth(numberOfExpirments); renderTexture(gl,
		 * awtTexture, 0, 0, glWidth, glHeight);
		 * 
		 * 
		 * 
		 * 
		 * // getNewScreenShots { gl.glReadBuffer(GL2.GL_BACK); ByteBuffer
		 * screenShotByteBuffer = null; BufferedImage screenShotImage = null;
		 * screenShotImage = new BufferedImage(width, height,
		 * BufferedImage.TYPE_4BYTE_ABGR);
		 * 
		 * screenShotByteBuffer = ByteBuffer .wrap(((DataBufferByte)
		 * screenShotImage.getRaster() .getDataBuffer()).getData());
		 * 
		 * gl.glReadBuffer(GL2.GL_BACK); gl.glReadPixels(x, y, width, height,
		 * GL2.GL_ABGR_EXT, GL2.GL_UNSIGNED_BYTE, screenShotByteBuffer);
		 * 
		 * try { ImageIO.write(screenShotImage, "png", new File(
		 * "C:\\Documents and Settings\\Clemens\\bild2.png")); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * }
		 */
		gl.glReadBuffer(GL2.GL_FRONT);
		gl.glDrawBuffer(GL2.GL_FRONT);

	}

	public final static BufferedImage resizeImageByFactor(BufferedImage image,
			double factor) {
		int width = (int) (image.getWidth() * factor);
		int height = (int) (image.getHeight() * factor);
		BufferedImage newimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		newimage.createGraphics().drawImage(image, 0, 0, width, height, null);

		return newimage;
	}

	private int genFBO(GL2 gl) {
		int[] array = new int[1];
		IntBuffer ib = IntBuffer.wrap(array);
		gl.glGenFramebuffers(1, ib);
		return ib.get(0);
	}

}
