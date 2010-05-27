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

package com.jmex.awt.swingui;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Logger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jme.image.Image;
import com.jme.util.geom.BufferUtils;
import com.jme.math.FastMath;

public abstract class ImageGraphicsBaseImpl extends ImageGraphics {
    protected static final Logger logger = Logger.getLogger(ImageGraphics.class.getName());
    protected final BufferedImage awtImage;
    protected final Graphics2D delegate;
    protected final byte[] data;
    protected final Rectangle dirty;
    protected final Point translation = new Point();
    protected ByteBuffer tmp_byteBuffer;
    protected final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    protected final int paintedMipMapCount;
    protected int mipMapLevel = 0;
    protected ImageGraphicsBaseImpl mipMapChild;
    protected boolean glTexSubImage2DSupported = true;
    protected IntBuffer idBuff = BufferUtils.createIntBuffer(16);
    private Rectangle imageBounds;
    private Rectangle clip = new Rectangle();
    private Rectangle tmp_dirty = new Rectangle();
    protected AffineTransform tx = new AffineTransform();
    protected float scaleX = 1;
    protected float scaleY = 1;

    protected ImageGraphicsBaseImpl( BufferedImage awtImage, byte[] data, Graphics2D delegate,
                                com.jme.image.Image image, Rectangle dirty,
                                int translationX, int translationY,
                                float scaleX, float scaleY, int mipMapCount,
                                ImageGraphicsBaseImpl mipMapChild, int mipMapLevel ) {
        super( image );
        this.awtImage = awtImage;
        this.data = data;
        this.delegate = delegate;
        this.dirty = dirty;
        translation.x = translationX;
        translation.y = translationY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.paintedMipMapCount = mipMapCount;
        this.mipMapChild = mipMapChild;
        this.mipMapLevel = mipMapLevel;
    }

    public ImageGraphicsBaseImpl( int width, int height, int paintedMipMapCount, int mipMapLevel, float scale ) {

        super( new com.jme.image.Image() );
        if ( paintedMipMapCount > 1 && ( !FastMath.isPowerOfTwo( width ) || !FastMath.isPowerOfTwo( height ) ) ) {
            throw new IllegalArgumentException( "Size must be power of 2 if mipmaps should be generated" );
        }
        awtImage = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        // Get a pointer to the image memory
        ByteBuffer scratch = BufferUtils.createByteBuffer(4 * width * height );
        tmp_byteBuffer = BufferUtils.createByteBuffer(4 * width * height );
        data = (byte[]) awtImage.getRaster().getDataElements( 0, 0,
                awtImage.getWidth(), awtImage.getHeight(), null );
        scratch.clear();
        scratch.put( data );
        scratch.flip();
        image.setFormat( Image.Format.RGBA8 );
        image.setWidth( width );
        image.setHeight( height );
        image.setData( scratch );

        delegate = (Graphics2D) awtImage.getGraphics();
        dirty = new Rectangle( 0, 0, width, height );

        this.mipMapLevel = mipMapLevel;
        scale( scale, scale );
        this.paintedMipMapCount = paintedMipMapCount;

        setBackground( TRANSPARENT );
    }

    /**
     * This method allows access to internal data of this class. Use for reading only. (Don't expect any
     * direct modifications on this image to take effect immediately.)
     * @return the BufferedImage used internally to draw at before updating the native image
     */
    public BufferedImage getAwtImage()
    {
       return awtImage;
    }

    protected Rectangle getImageBounds() {
        if ( imageBounds == null ) {
            imageBounds = new Rectangle( 0, 0, getImage().getWidth(), getImage().getHeight() );
        }
        return imageBounds;
    }

    public boolean isDirty() {
        return !dirty.isEmpty();
    }

    protected void makeDirty( int x, int y, int width, int height ) {
        if ( width < 0 ) {
            x = x + width;
            width = -width;
        }
        if ( height < 0 ) {
            y = y + height;
            height = -height;
        }
        tmp_dirty.setBounds( x, y, width, height );
        makeDirty( tmp_dirty );
    }

    private void makeDirty( Rectangle rectangle ) {
        synchronized ( dirty ) {

            getClipBounds( clip );

            //debug-:
//            final StackTraceElement[] stackTrace = new Exception().getStackTrace();
//            for ( int i=0; i < stackTrace.length; i++ )
//            {
//                final String methodName = stackTrace[i].getMethodName();
//                if ( !"makeDirty".equals( methodName ) )
//                {
//                    logger.info( methodName +"["+ stackTrace[i].getLineNumber() + "]" );
//                    break;
//                }
//            }
            Rectangle2D.intersect( clip, rectangle, rectangle );
            if ( !rectangle.isEmpty() ) {
                rectangle.x *= scaleX;
                rectangle.y *= scaleY;
                rectangle.width *= scaleX;
                rectangle.height *= scaleY;
                rectangle.translate( translation.x, translation.y );
                Rectangle2D.intersect( rectangle, getImageBounds(), rectangle );
                if ( !rectangle.isEmpty() ) {
                    if ( !dirty.isEmpty() ) {
                        dirty.add( rectangle );
                    }
                    else {
                        dirty.setBounds( rectangle );
                    }
                }
            }
        }
    }

    /**
     * todo: don't be lazy - compute the actual area!
     */
    private void makeDirty() {
        makeDirty( 0, 0, getImage().getWidth(), getImage().getHeight() );
    }

    public Color getColor() {
        return delegate.getColor();
    }

    public void setColor( Color c ) {
        if ( mipMapChild != null ) {
            mipMapChild.setColor( c );
        }
        delegate.setColor( c );
    }

    public void setPaintMode() {
        if ( mipMapChild != null ) {
            mipMapChild.setPaintMode();
        }
        delegate.setPaintMode();
    }

    public void setXORMode( Color c1 ) {
        if ( mipMapChild != null ) {
            mipMapChild.setXORMode( c1 );
        }
        delegate.setXORMode( c1 );
    }

    public Font getFont() {
        return delegate.getFont();
    }

    public void setFont( Font font ) {
        if ( mipMapChild != null ) {
            mipMapChild.setFont( font );
        }
        delegate.setFont( font );
    }

    public FontMetrics getFontMetrics( Font f ) {
        return delegate.getFontMetrics( f );
    }

    public Rectangle getClipBounds() {
        return delegate.getClipBounds();
    }

    public void clipRect( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.clipRect( x, y, width, height );
        }
        delegate.clipRect( x, y, width, height );
    }

    public void setClip( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.setClip( x, y, width, height );
        }
        delegate.setClip( x, y, width, height );
    }

    public Shape getClip() {
        return delegate.getClip();
    }

    public void setClip( Shape clip ) {
        if ( mipMapChild != null ) {
            mipMapChild.setClip( clip );
        }
        delegate.setClip( clip );
    }

    public void copyArea( int x, int y, int width, int height, int dx, int dy ) {
        if ( mipMapChild != null ) {
            mipMapChild.copyArea( x, y, width, height, dx, dy );
        }
        synchronized ( dirty ) {
            makeDirty( x + dx, y + dy, width, height );
            delegate.copyArea( x, y, width, height, dx, dy );
        }
    }

    public void drawLine( int x1, int y1, int x2, int y2 ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawLine( x1, y1, x2, y2 );
        }
        synchronized ( dirty ) {
            makeDirty( x1, y1, x2 - x1, y2 - y1 );
            delegate.drawLine( x1, y1, x2, y2 );
        }
    }

    public void fillRect( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.fillRect( x, y, width, height );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillRect( x, y, width, height );
        }
    }

    public void clearRect( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.clearRect( x, y, width, height );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            //works in JDK1.5:
//            delegate.clearRect( x, y, width, height );

            //fix for bug in JDK1.4:
            Color color = delegate.getColor();
            delegate.setColor( delegate.getBackground() );
            Composite composite = delegate.getComposite();
            delegate.setComposite( AlphaComposite.Clear );
            delegate.fillRect( x, y, width, height );
            delegate.setComposite( composite );
            delegate.setColor( color );
        }
    }

    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
    }

    public void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        if ( mipMapChild != null ) {
            mipMapChild.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
    }

    public void drawOval( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawOval( x, y, width, height );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawOval( x, y, width, height );
        }
    }

    public void fillOval( int x, int y, int width, int height ) {
        if ( mipMapChild != null ) {
            mipMapChild.fillOval( x, y, width, height );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillOval( x, y, width, height );
        }
    }

    public void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawArc( x, y, width, height, startAngle, arcAngle );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawArc( x, y, width, height, startAngle, arcAngle );
        }
    }

    public void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
        if ( mipMapChild != null ) {
            mipMapChild.fillArc( x, y, width, height, startAngle, arcAngle );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillArc( x, y, width, height, startAngle, arcAngle );
        }
    }

    public void drawPolyline( int[] xPoints, int[] yPoints, int nPoints ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawPolyline( xPoints, yPoints, nPoints );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawPolyline( xPoints, yPoints, nPoints );
        }
    }

    public void drawPolygon( int[] xPoints, int[] yPoints, int nPoints ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawPolygon( xPoints, yPoints, nPoints );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawPolygon( xPoints, yPoints, nPoints );
        }
    }

    public void fillPolygon( int[] xPoints, int[] yPoints, int nPoints ) {
        if ( mipMapChild != null ) {
            mipMapChild.fillPolygon( xPoints, yPoints, nPoints );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.fillPolygon( xPoints, yPoints, nPoints );
        }
    }

    public boolean drawImage( java.awt.Image img, int x, int y, ImageObserver observer ) {
        makeDirty( x, y, img.getWidth( observer ), img.getHeight( observer ) );
        synchronized ( dirty ) {
            if ( mipMapChild != null ) {
                mipMapChild.drawImage( img, x, y, observer );
            }
            return delegate.drawImage( img, x, y, observer );
        }
    }

    public boolean drawImage( java.awt.Image img, int x, int y, int width, int height, ImageObserver observer ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, x, y, width, height, observer );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            return delegate.drawImage( img, x, y, width, height, observer );
        }
    }

    public boolean drawImage( java.awt.Image img, int x, int y, Color bgcolor, ImageObserver observer ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, x, y, bgcolor, observer );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, img.getWidth( observer ), img.getHeight( observer ) );
            return delegate.drawImage( img, x, y, bgcolor, observer );
        }
    }

    public boolean drawImage( java.awt.Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, x, y, width, height, bgcolor, observer );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            return delegate.drawImage( img, x, y, width, height, bgcolor, observer );
        }
    }

    public boolean drawImage( java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer );
        }
        synchronized ( dirty ) {
            makeDirty( dx1, dy1, dx2 - dx1, dy2 - dy1 );
            return delegate.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer );
        }
    }

    public boolean drawImage( java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer );
        }
        synchronized ( dirty ) {
            makeDirty( dx1, dy1, dx2 - dx1, dy2 - dy1 );
            return delegate.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer );
        }
    }

    public void dispose() {
        if ( mipMapChild != null ) {
            mipMapChild.dispose();
        }
        delegate.dispose();
    }

    public void draw( Shape s ) {
        if ( mipMapChild != null ) {
            mipMapChild.draw( s );
        }
        synchronized ( dirty ) {
            makeDirty( s.getBounds() );
            delegate.draw( s );
        }
    }

    public boolean drawImage( java.awt.Image img, AffineTransform xform, ImageObserver obs ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, xform, obs );
        }
        synchronized ( dirty ) {
            makeDirty();
            return delegate.drawImage( img, xform, obs );
        }
    }

    public void drawImage( BufferedImage img, BufferedImageOp op, int x, int y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawImage( img, op, x, y );
        }
        synchronized ( dirty ) {
            makeDirty( x, y, img.getWidth( null ), img.getHeight( null ) );
            delegate.drawImage( img, op, x, y );
        }
    }

    public void drawRenderedImage( RenderedImage img, AffineTransform xform ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawRenderedImage( img, xform );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawRenderedImage( img, xform );
        }
    }

    public void drawRenderableImage( RenderableImage img, AffineTransform xform ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawRenderableImage( img, xform );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawRenderableImage( img, xform );
        }
    }

    public void drawString( String str, int x, int y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawString( str, x, y );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( str, x, y );
        }
    }

    public void drawString( String s, float x, float y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawString( s, x, y );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( s, x, y );
        }
    }

    public void drawString( AttributedCharacterIterator iterator, int x, int y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawString( iterator, x, y );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( iterator, x, y );
        }
    }

    public void drawString( AttributedCharacterIterator iterator, float x, float y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawString( iterator, x, y );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( iterator, x, y );
        }
    }

    public void drawGlyphVector( GlyphVector g, float x, float y ) {
        if ( mipMapChild != null ) {
            mipMapChild.drawGlyphVector( g, x, y );
        }
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawGlyphVector( g, x, y );
        }
    }

    public void fill( Shape s ) {
        if ( mipMapChild != null ) {
            mipMapChild.fill( s );
        }
        synchronized ( dirty ) {
            makeDirty( s.getBounds() );
            delegate.fill( s );
        }
    }

    public boolean hit( Rectangle rect, Shape s, boolean onStroke ) {
        return delegate.hit( rect, s, onStroke );
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return delegate.getDeviceConfiguration();
    }

    public void setComposite( Composite comp ) {
        if ( mipMapChild != null ) {
            mipMapChild.setComposite( comp );
        }
        delegate.setComposite( comp );
    }

    public void setPaint( Paint paint ) {
        if ( mipMapChild != null ) {
            mipMapChild.setPaint( paint );
        }
        delegate.setPaint( paint );
    }

    public void setStroke( Stroke s ) {
        if ( mipMapChild != null ) {
            mipMapChild.setStroke( s );
        }
        delegate.setStroke( s );
    }

    public void setRenderingHint( RenderingHints.Key hintKey, Object hintValue ) {
        if ( mipMapChild != null ) {
            mipMapChild.setRenderingHint( hintKey, hintValue );
        }
        delegate.setRenderingHint( hintKey, hintValue );
    }

    public Object getRenderingHint( RenderingHints.Key hintKey ) {
        return delegate.getRenderingHint( hintKey );
    }

    public void setRenderingHints( Map<?,?> hints ) {
        if ( mipMapChild != null ) {
            mipMapChild.setRenderingHints( hints );
        }
        delegate.setRenderingHints( hints );
    }

    public void addRenderingHints( Map<?,?> hints ) {
        if ( mipMapChild != null ) {
            mipMapChild.addRenderingHints( hints );
        }
        delegate.addRenderingHints( hints );
    }

    public RenderingHints getRenderingHints() {
        return delegate.getRenderingHints();
    }

    public void translate( int x, int y ) {
        translation.x += x * scaleX;
        translation.y += y * scaleY;
        if ( mipMapChild != null ) {
            mipMapChild.translate( x, y );
        }
        delegate.translate( x, y );
    }

    public void translate( double tx, double ty ) {
        translation.x += tx * scaleX;
        translation.y += ty * scaleY;
        if ( mipMapChild != null ) {
            mipMapChild.translate( tx, ty );
        }
        delegate.translate( tx, ty );
    }

    public void rotate( double theta ) {
        throw new UnsupportedOperationException();
//        delegate.rotate( theta );
    }

    public void rotate( double theta, double x, double y ) {
        throw new UnsupportedOperationException();
//        delegate.rotate( theta, x, y );
    }

    public void scale( double sx, double sy ) {
        scaleX *= sx;
        scaleY *= sy;
        if ( mipMapChild != null ) {
            mipMapChild.scale( sx, sy );
        }
        delegate.scale( sx, sy );
    }

    public void shear( double shx, double shy ) {
        throw new UnsupportedOperationException();
//        delegate.shear( shx, shy );
    }

    public void transform( AffineTransform Tx ) {
        tx.setTransform(Tx);
        delegate.transform( Tx );
    }

    public void setTransform( AffineTransform Tx ) {
        tx.setTransform(Tx);
        delegate.setTransform( Tx );
    }

    public AffineTransform getTransform() {
        return delegate.getTransform();
    }

    public Paint getPaint() {
        return delegate.getPaint();
    }

    public Composite getComposite() {
        return delegate.getComposite();
    }

    public void setBackground( Color color ) {
        if ( mipMapChild != null ) {
            mipMapChild.setBackground( color );
        }
        delegate.setBackground( color );
    }

    public Color getBackground() {
        return delegate.getBackground();
    }

    public Stroke getStroke() {
        return delegate.getStroke();
    }

    public void clip( Shape s ) {
        if ( mipMapChild != null ) {
            mipMapChild.clip( s );
        }
        delegate.clip( s );
    }

    public FontRenderContext getFontRenderContext() {
        return delegate.getFontRenderContext();
    }

    public void update() {
        synchronized (dirty) {
            if (dirty == null) {
                awtImage.getRaster().getDataElements(0, 0, awtImage.getWidth(),
                        awtImage.getHeight(), data);
                ByteBuffer scratch = getImage().getData(0);
                scratch.clear();
                scratch.put(data);
                scratch.flip();
            } else {
                awtImage.getRaster().getDataElements(dirty.x, dirty.y,
                        dirty.width, dirty.height, data);
                ByteBuffer scratch = getImage().getData(0);
                int pixLen = awtImage.getRaster().getNumDataElements();
                for (int dirtyRow = 0; dirtyRow < dirty.height; ++dirtyRow) {
                    scratch
                            .position(((image.getWidth() * (dirty.y + dirtyRow)) + dirty.x)
                                    * pixLen);
                    scratch.put(data, dirty.width * dirtyRow * pixLen,
                            dirty.width * pixLen);
                }
            }
        }
    }
}

/*
 * $Log$
 */

