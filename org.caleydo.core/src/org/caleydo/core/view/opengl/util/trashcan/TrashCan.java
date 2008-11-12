package org.caleydo.core.view.opengl.util.trashcan;

import javax.media.opengl.GL;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.data.loader.ResourceLoader;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class TrashCan
{

	private static String TRASH_CAN_PATH = "resources/icons/view/remote/trashcan_empty.png";

	private Texture trashCanTexture;

	public void init(final GL gl)
	{
		trashCanTexture = GeneralManager.get().getResourceLoader().getTexture(TRASH_CAN_PATH);
	}

	public void render(final GL gl, final ARemoteViewLayoutRenderStyle layoutStyle)
	{

		if (trashCanTexture == null)
			return;

		TextureCoords texCoords = trashCanTexture.getImageTexCoords();

		trashCanTexture.enable();
		trashCanTexture.bind();

		gl.glColor3f(1, 1, 1);

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(layoutStyle.getTrashCanXPos(), layoutStyle.getTrashCanYPos(), 4.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(layoutStyle.getTrashCanXPos() + layoutStyle.getTrashCanWidth(),
				layoutStyle.getTrashCanYPos(), 4.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(layoutStyle.getTrashCanXPos() + layoutStyle.getTrashCanWidth(),
				layoutStyle.getTrashCanYPos() + layoutStyle.getTrashCanHeight(), 4.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(layoutStyle.getTrashCanXPos(), layoutStyle.getTrashCanYPos()
				+ layoutStyle.getTrashCanHeight(), 4.01f);
		gl.glEnd();

		trashCanTexture.disable();
	}
}
