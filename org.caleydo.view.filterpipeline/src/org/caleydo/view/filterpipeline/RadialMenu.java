package org.caleydo.view.filterpipeline;

import gleem.linalg.Vec2f;

import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class RadialMenu
{
	private Vec2f position = new Vec2f();
	private List<Texture> entries = new LinkedList<Texture>();
	private Texture entryBackground = null;
	
	private final float entryDistance = 0.5f;
	private final float entryHalfSize = 0.1f;
	private final float backgroundHalfSize = 1.8f * entryHalfSize;

	private IRadialMenuListener listener;
	private int externalId;
	
	private boolean visible = false;
	private int activeEntry = -1;
	
	/**
	 * 
	 * @param listener
	 * @param entryBackground 
	 */
	public RadialMenu(IRadialMenuListener listener, Texture entryBackground)
	{
		this.listener = listener;
		this.entryBackground = entryBackground;
	}

	/**
	 * 
	 * @param gl
	 */
	public void render(GL2 gl)
	{
		if( !visible )
			return;
		
		float angle = (float)((2 * Math.PI)/entries.size());
		
		gl.glMatrixMode(GL2.GL_MODELVIEW_MATRIX);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glTranslatef(position.x(), position.y(), 0.95f);
		
		gl.glBegin(GL2.GL_LINES);
		{
			for( int i = 0; i < entries.size(); ++i )
			{
				if( i == activeEntry )
					gl.glColor3f(1.f, 0.2f, 0.2f);
				else
					gl.glColor3f(0.2f, 0.2f, 0.2f);
				
				gl.glVertex2f(0, 0);
				gl.glVertex2d
				(
					(entryDistance - backgroundHalfSize) * Math.cos(i * angle),
					(entryDistance - backgroundHalfSize) * Math.sin(i * angle)
				);
			}
		}
		gl.glEnd();
		
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		
		for( int i = 0; i < entries.size(); ++i )
		{
			gl.glPushMatrix();
			gl.glTranslated
			(
				entryDistance * Math.cos(i * angle),
				entryDistance * Math.sin(i * angle),
				0
			);
			
			if( entryBackground != null )
			{
				entryBackground.enable();
				entryBackground.bind();
				TextureCoords texCoords = entryBackground.getImageTexCoords();
				
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				
				gl.glBegin(GL2.GL_QUADS);
				{
					if( i == activeEntry )
						gl.glColor4f(1.0f, 0.6f, 0.6f, 0.7f);
					else
						gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
					
					gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
					gl.glVertex2f(-backgroundHalfSize, -backgroundHalfSize);
					
					gl.glTexCoord2f(texCoords.left(), texCoords.top());
					gl.glVertex2f(-backgroundHalfSize,  backgroundHalfSize);
					
					gl.glTexCoord2f(texCoords.right(), texCoords.top());
					gl.glVertex2f( backgroundHalfSize,  backgroundHalfSize);
					
					gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
					gl.glVertex2f( backgroundHalfSize, -backgroundHalfSize);
				}				
				gl.glEnd();
				
				entryBackground.disable();
			}
			
			Texture texture = entries.get(i);
			TextureCoords texCoords = new TextureCoords(0, 0, 1, 1);
			
			if(	texture != null )
			{
				texture.enable();
				texture.bind();
				texCoords = texture.getImageTexCoords();
				
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			}
			
			gl.glBegin(GL2.GL_QUADS);
			{
				if( texture == null )
				{
					if( i == activeEntry )
						gl.glColor3f(1.f, 0.2f, 0.2f);
					else
						gl.glColor3f(0.2f, 0.2f, 0.2f);
				}
				else
				{
					if( i == activeEntry )
						gl.glColor3f(1f, 0.4f, 0.4f);
					else
						gl.glColor3f(1, 1, 1);
				}
				
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex2f(-entryHalfSize, -entryHalfSize);
				
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex2f(-entryHalfSize,  entryHalfSize);
				
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex2f( entryHalfSize,  entryHalfSize);
				
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex2f( entryHalfSize, -entryHalfSize);
			}
			gl.glEnd();
			
			if( texture != null )
				texture.disable();
			
			gl.glPopMatrix();
		}

		gl.glPopAttrib();
		gl.glPopMatrix();
	}
	
	/**
	 * 
	 * @param gl
	 * @param mousePos
	 */
	public void handleDragging(Vec2f mousePos)
	{
		if( !visible )
			return;
		
		Vec2f dir = mousePos.copy();
		dir.sub(position);
		
		int newEntry = -1;
		
		// no selection if over center
		if( dir.length() <= entryDistance/4 )
		{
			newEntry = -1;
		}
		else
		{
	
			dir.normalize();		
			double mouseAngle = Math.acos(dir.dot(new Vec2f(1,0)));
			
			if( dir.y() < 0 )
				mouseAngle = 2 * Math.PI - mouseAngle;
	
			newEntry = (int)Math.round(mouseAngle / (2*Math.PI/entries.size()))
			         % entries.size();
		}
		
		if( activeEntry != newEntry )
		{
			activeEntry = newEntry;
			listener.handleRadialMenuHover(externalId, newEntry);
		}
	}
		

	/**
	 * 
	 */
	public void handleMouseReleased()
	{
		if( !visible )
			return;
		
		listener.handleRadialMenuSelection(externalId, activeEntry);
		
		activeEntry = -1;
		visible = false;
	}

	/**
	 * 
	 * @param iExternalID
	 * @param mousePosition 
	 */
	public void show(int iExternalID, Vec2f mousePosition)
	{
		externalId = iExternalID;
		position = mousePosition.copy();
		visible = true;
	}

	/**
	 * 
	 * @param icon
	 */
	public void addEntry(Texture icon)
	{
		entries.add(icon);
	}
}
