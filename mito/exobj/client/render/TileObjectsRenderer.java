package com.mito.exobj.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.VBOHandler;
import com.mito.exobj.common.block.TileObjects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileObjectsRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float part) {
		if(tile instanceof TileObjects){
			TileObjects to = (TileObjects) tile;
			if(to.name != null){
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				BB_RenderHandler.enableClient();
				List<ExtraObject> list = to.name.list;

				Minecraft.getMinecraft().entityRenderer.enableLightmap((double) part);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				to.buffer.draw();

				BB_RenderHandler.disableClient();
				if (to.shouldUpdateRender) {
					to.buffer.delete();
					CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
					c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
					c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
					for (int n = 0; n < list.size(); n++) {
						ExtraObject base = list.get(n);
						BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
						render.updateRender(c, base, part);
					}
					to.shouldUpdateRender = false;
					VBOHandler vbo = c.end();
					to.buffer.add(vbo);
				}
				Minecraft.getMinecraft().entityRenderer.disableLightmap((double) part);
				GL11.glPopMatrix();
			}
		}
	}

}
