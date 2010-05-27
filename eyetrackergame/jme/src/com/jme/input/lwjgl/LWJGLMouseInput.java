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

package com.jme.input.lwjgl;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.jme.image.Image;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.system.lwjgl.LWJGLStandardCursor;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * Mouse input handler that uses the LWJGL input API.
 *
 * @see Cursor
 * @see Mouse
 * @author Mark Powell
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class LWJGLMouseInput extends MouseInput {
    private static final Logger logger = Logger.getLogger(LWJGLMouseInput.class.getName());

    // The URIs are stored in string form because the URL class makes a call
    // out to the network every time equals() is called - not good far map keys!
    /** A map of URIs to cached cursor instances. */
    private static Map<String, Cursor> loadedCursors;

    private static String urlToString(URL url) {
        try {
            return url.toURI().normalize().toString();
        } catch (URISyntaxException e) {
            // Fall back to using the externalized URL, this will be fine if client
            // code uses the same URL each time but issue a warning anyway.
            logger.log(Level.WARNING, "URL does not comply with RFC2396 - " + url, e);
            return url.toExternalForm();
        }
    }
    
	private int dx, dy;
	private int lastX, lastY;
	private boolean virgin = true;
	private int dWheel;
	private int wheelRotation;
	
	private boolean[] buttonPressed;

	/**
	 * Constructor creates a new <code>LWJGLMouseInput</code> object. A call
	 * to the LWJGL creation method is made, if any problems occur during
	 * this creation, it is logged.
	 *
	 */
	protected LWJGLMouseInput() {
		try {
			Mouse.create();
			setCursorVisible(false);
			buttonPressed = new boolean[Mouse.getButtonCount()];
		} catch (Exception e) {
			logger.log(Level.WARNING, "Problem during creation of Mouse.", e);
		}
	}

	/**
	 * <code>destroy</code> cleans up the native mouse reference.
	 * @see com.jme.input.MouseInput#destroy()
	 */
	public void destroy() {
		setCursorVisible(false);
		Mouse.destroy();

	}

	/**
	 * <code>getButtonIndex</code> returns the index of a given button name.
	 * @see com.jme.input.MouseInput#getButtonIndex(java.lang.String)
	 */
	public int getButtonIndex(String buttonName) {
		return Mouse.getButtonIndex(buttonName);
	}

	/**
	 * <code>getButtonName</code> returns the name of a given button index.
	 * @see com.jme.input.MouseInput#getButtonName(int)
	 */
	public String getButtonName(int buttonIndex) {
		return Mouse.getButtonName(buttonIndex);
	}

	/**
	 * <code>isButtonDown</code> tests if a given button is pressed or not.
	 * @see com.jme.input.MouseInput#isButtonDown(int)
	 */
	public boolean isButtonDown(int buttonCode) {
		return getButtonState(buttonCode);
	}

	/**
	 * <code>getWheelDelta</code> retrieves the change of the mouse wheel,
	 * if any.
	 * @see com.jme.input.MouseInput#getWheelDelta()
	 */
	public int getWheelDelta() {
		return dWheel;
	}
	/**
	 * <code>getXDelta</code> retrieves the change of the x position, if any.
	 * @see com.jme.input.MouseInput#getXDelta()
	 */
	public int getXDelta() {
		return dx;
	}
	/**
	 * <code>getYDelta</code> retrieves the change of the y position, if any.
	 * @see com.jme.input.MouseInput#getYDelta()
	 */
	public int getYDelta() {
		return dy;
	}

	/**
	 * <code>getXAbsolute</code> gets the absolute x axis value.
	 * @see com.jme.input.MouseInput#getXAbsolute()
	 */
	public int getXAbsolute() {
		return Mouse.getX();
	}

	/**
	 * <code>getYAbsolute</code> gets the absolute y axis value.
	 * @see com.jme.input.MouseInput#getYAbsolute()
	 */
	public int getYAbsolute() {
		return Mouse.getY();
	}

	/**
	 * <code>updateState</code> updates the mouse state.
	 * @see com.jme.input.MouseInput#update()
	 */
	public void update() {
		/**Actual polling is done in {@link org.lwjgl.opengl.Display#update()} */
        if (Display.isActive()) {
    		boolean grabbed = Mouse.isGrabbed();
    		int x;
    		int y;
    		if ( grabbed ) {
    			dx = Mouse.getDX();
    			dy = Mouse.getDY();
    			x = Mouse.getX();
    			y = Mouse.getY();
    		} else {
    			x = Mouse.getX();
    			y = Mouse.getY();
    			dx = x - lastX;
    			dy = y - lastY;
    			lastX = x;
    			lastY = y;
    		}
    		if (virgin && (dx != 0 || dy != 0)) {
    			dx = dy = 0;
    			wheelRotation = 0;
    			virgin = false;
    		}
    		dWheel = Mouse.getDWheel();
    		wheelRotation += dWheel;
    


			if (listeners != null && listeners.size() > 0) {
				while (Mouse.next()) {
					int button = Mouse.getEventButton();
					boolean pressed = button >= 0
							&& Mouse.getEventButtonState();
					setButtonState(button, pressed);

					int wheelDelta = Mouse.getEventDWheel();

					int xDelta = Mouse.getEventDX();
					int yDelta = Mouse.getEventDY();

					if (!grabbed) { // event x and y should come from event
						x = Mouse.getEventX();
						y = Mouse.getEventY();
					}
					for (int i = 0; i < listeners.size(); i++) {
						MouseInputListener listener = listeners.get(i);
						if (button >= 0) {
							listener.onButton(button, pressed, x, y);
						}
						if (wheelDelta != 0) {
							listener.onWheel(wheelDelta, x, y);
						}
						if (xDelta != 0 || yDelta != 0) {
							listener.onMove(xDelta, yDelta, x, y);
						}
					}
				}
				return;
			}
        }

		// clear events - could use a faster method in lwjgl here...
		while ( Mouse.next() ) {
			int button = Mouse.getEventButton();
			boolean pressed = button >= 0
					&& Mouse.getEventButtonState();
			setButtonState(button, pressed);
		}
	}


	/**
	 * <code>setCursorVisible</code> sets the visiblity of the hardware
	 * cursor.
	 * 
	 * @see com.jme.input.MouseInput#setCursorVisible(boolean)
	 */
	public void setCursorVisible(boolean v) {
	  Mouse.setGrabbed(!v);
		try {

			if (v) {
				Mouse.setNativeCursor(LWJGLStandardCursor.cursor);
			} else {
				Mouse.setNativeCursor(null);
			}

		} catch (Exception e) {
			logger.warning("Problem showing mouse cursor.");
		}
	}

	/**
	 * <code>isCursorVisible</code> Returns true if a cursor is currently bound.
	 * @see com.jme.input.MouseInput#isCursorVisible()
	 */
	public boolean isCursorVisible() {
		return Mouse.getNativeCursor() != null;
	}

	public void setHardwareCursor(URL file) {
		setHardwareCursor(file, -1, -1);
	}

    /**
        * Loads and sets a hardware cursor
        * 
        * @param url to imagefile 
        * @param xHotspot from image left
        * @param yHotspot from image bottom
        */
	public synchronized void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
        if (loadedCursors == null) {
            loadedCursors = new HashMap<String, Cursor>();
        }
        final String fileURI = urlToString(file);

        Cursor cursor = loadedCursors.get(fileURI);
		if (cursor == null) {
            boolean eightBitAlpha =
                (Cursor.getCapabilities() & Cursor.CURSOR_8_BIT_ALPHA) != 0;

            Image image = TextureManager.loadImage(file, true);
            boolean isRgba = image.getFormat() == Image.Format.RGBA8;
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			
            ByteBuffer imageData = image.getData(0);
            imageData.rewind();
            IntBuffer imageDataCopy = BufferUtils.createIntBuffer(imageWidth * imageHeight);
			
			for (int y = 0; y < imageHeight; y++) {
				for (int x = 0; x < imageWidth; x++) {
					int index = y * imageWidth + x;

                    int r = imageData.get() & 0xff;
                    int g = imageData.get() & 0xff;
                    int b = imageData.get() & 0xff;
                    int a = 0xff;
                    if (isRgba) {
                        a = imageData.get() & 0xff;
                        if (!eightBitAlpha) {
                            if (a < 0x7f) {
                                a = 0x00;
                                // small hack to prevent triggering "reverse screen" on windows.
                                r = g = b = 0;
                            }
                            else {
                                a = 0xff;
                            }
                        }
                    }

                    imageDataCopy.put(index, (a << 24) | (r << 16) | (g << 8) | b);
				}
			}

            if (xHotspot < 0 || yHotspot < 0
                    || xHotspot >= imageWidth
                    || yHotspot >= imageHeight) {
                // Revert to a hotspot position of top-left
                xHotspot = 0;
                yHotspot = imageHeight - 1;
                logger.log(Level.WARNING,
                        "Hotspot positions are outside image bounds!");
            }

			try {
				cursor = new Cursor(imageWidth, imageHeight, xHotspot, yHotspot, 1, imageDataCopy, null);
			} catch (LWJGLException e) {
				logger.log(Level.WARNING, "Failed creating native cursor!", e);
			}

			loadedCursors.put(fileURI, cursor);
		}
		try {
		    if (!cursor.equals(Mouse.getNativeCursor())) {
		        Mouse.setNativeCursor(cursor);
		    }
		} catch (LWJGLException e) {
			logger.log(Level.WARNING, "Failed setting native cursor!", e);
		}
	}

    /**
    * This method will set an animated harware cursor.
    * 
    * @param file in this method file is only used as a key for cursor cashing 
    * @param images the animation frames
    * @param delays delays between changing each frame
    * @param xHotspot from image left
    * @param yHotspot from image bottom
    */
    public synchronized void setHardwareCursor(URL file, Image[] images, int[] delays,
            int xHotspot, int yHotspot) {
        if (loadedCursors == null) {
            loadedCursors = new HashMap<String, Cursor>();
        }
        final String fileURI = urlToString(file);

        Cursor cursor = loadedCursors.get(fileURI);
        if (cursor == null) {
            boolean eightBitAlpha =
                (Cursor.getCapabilities() & Cursor.CURSOR_8_BIT_ALPHA) != 0;

            Image image = images[0];
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            IntBuffer imageData = image.getData(0).asIntBuffer();
            int imageSize = imageData.remaining();

            IntBuffer cursorData = BufferUtils.createIntBuffer(imageSize
                    * images.length);

            for (int i = 0; i < images.length; i++) {
                image = images[i];
                imageData = image.getData(0).asIntBuffer();

                for (int y = 0; y < imageHeight; y++) {
                    for (int x = 0; x < imageWidth; x++) {
                        int index = y * imageWidth + x;

                        int pixel = imageData.get(index);
                        int a = (pixel >> 24) & 0xff;
                        int b = (pixel >> 16) & 0xff;
                        int g = (pixel >> 8) & 0xff;
                        int r = (pixel) & 0xff;
                        if (!eightBitAlpha) {
                            if (a < 0x7f) {
                                a = 0x00;
                                // small hack to prevent triggering "reverse screen" on windows.
                                r = g = b = 0;
                            } else {
                                a = 0xff;
                            }
                        }

                        cursorData.put(index + imageSize * i, (a << 24)
                                | (r << 16) | (g << 8) | b);
                    }
                }

            }

            if (xHotspot < 0 || yHotspot < 0
                    || xHotspot >= imageWidth
                    || yHotspot >= imageHeight) {
                // Revert to a hotspot position of top-left
                xHotspot = 0;
                yHotspot = imageHeight - 1;
                logger.log(Level.WARNING,
                        "Hotspot positions are outside image bounds!");
            }

            IntBuffer delaysData = null;
            if (delays != null) {
                delaysData = BufferUtils.createIntBuffer(delays.length);
                delaysData.put(delays);
                delaysData.rewind();
            }

            try {
                cursor = new Cursor(imageWidth, imageHeight, xHotspot,
                        yHotspot, images.length, cursorData, delaysData);
            } catch (LWJGLException e) {
                logger.log(Level.WARNING, "Failed creating native cursor!", e);
            }

            loadedCursors.put(fileURI, cursor);
        }

        try {
            org.lwjgl.input.Mouse.setNativeCursor(cursor);
        } catch (LWJGLException e) {
            logger.log(Level.WARNING, "Failed setting native cursor!", e);
        }

    }

	public int getWheelRotation() {
		return wheelRotation;
	}

	public int getButtonCount() {
		return Mouse.getButtonCount();
	}

	public void setCursorPosition( int x, int y) {
		Mouse.setCursorPosition( x, y);
	}
	
	public void clear() {
		Arrays.fill(buttonPressed, false);
	}
	
	public void clearButton(int buttonCode) {
		setButtonState(buttonCode, false);
	}
	
	private boolean getButtonState(int buttonCode) {
		if (buttonCode < 0) {
			return false;
		}
		checkButtonBounds(buttonCode);
		return buttonPressed[buttonCode];
	}
	
	private void setButtonState(int buttonCode, boolean pressed) {
		if (buttonCode >= 0) {
			checkButtonBounds(buttonCode);
			buttonPressed[buttonCode] = pressed;
		}
	}
	
	private void checkButtonBounds(int buttonCode) {
		if (buttonCode >= buttonPressed.length) {
			boolean[] newButtonPressed = new boolean[buttonCode + 1];
			System.arraycopy(buttonPressed, 0, newButtonPressed, 0, buttonPressed.length);
			buttonPressed = newButtonPressed;
		}
	}
}
