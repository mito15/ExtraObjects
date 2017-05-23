package com.mito.exobj.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.block.TileObjects;
import com.mito.exobj.item.ItemSelectTool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileObjectsRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float part) {
		if (tile instanceof TileObjects) {

			TileObjects to = (TileObjects) tile;
			if (to.name != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				BB_RenderHandler.enableClient();
				List<ExtraObject> list = to.name.list;

				Minecraft.getMinecraft().entityRenderer.enableLightmap((double) part);
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				to.buffer.draw();
				GL11.glPopMatrix();

				BB_RenderHandler.disableClient();
				if (to.shouldUpdateRender) {
					to.buffer.delete();
					CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
					c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
					c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
					for (ExtraObject base : list) {
						BB_Render render = BB_ResisteredList.getBraceBaseRender(base);
						render.updateRender(c, base, tile.xCoord, tile.yCoord, tile.zCoord);
					}
					to.shouldUpdateRender = false;
					VBOHandler vbo = c.end();
					to.buffer.add(vbo);
				}
				Minecraft.getMinecraft().entityRenderer.disableLightmap((double) part);
				if (Main.proxy.getClientPlayer().getCurrentEquippedItem() != null && Main.proxy.getClientPlayer().getCurrentEquippedItem().getItem() instanceof ItemSelectTool) {
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					RenderBlocks renderer = new RenderBlocks();
					
					GL11.glLineWidth(3.0F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
					
					Tessellator t = Tessellator.instance;
					GL11.glTranslated(0.5, 0.5, 0.5);
					renderBox(1.0, 1.0, 1.0, GL11.GL_LINE_LOOP);
					GL11.glTranslated(-0.5, -0.5, -0.5);
					for (ExtraObject base : list) {
						t.startDrawing(GL11.GL_LINE_LOOP);
						t.addVertex(0.5, 0.5, 0.5);
						t.addVertex(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
						t.draw();
					}
					GL11.glPopMatrix();
					
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}
				/*GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				GL11.glClearStencil(0);
				GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				GL11.glStencilFunc(GL11.GL_ALWAYS, 1, ~0);
				GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex3d(0, 0, 1);
				GL11.glVertex3d(1, 0, 1);
				GL11.glVertex3d(1, 1, 1);
				GL11.glVertex3d(0, 1, 1);
				GL11.glEnd();
				
				GL11.glColorMask(true, true, true, true);
				GL11.glDepthMask(true);
				
				GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
				GL11.glStencilFunc(GL11.GL_EQUAL, 1, ~0);
				
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glTranslated(0.5, 0.5, -0.5);
				for (int i = -4; i < 5; i++) {
					for (int i1 = -4; i1 < 5; i1++) {
						for (int i2 = 0; i2 < 9; i2++) {
							if (tile.getWorldObj().getBlock(tile.xCoord + i, tile.yCoord + i1, tile.zCoord + i2).getLightValue() > 8) {
								GL11.glPushMatrix();
								GL11.glTranslated(i, i1, -i2);
								this.renderBox(0.2, 0.2, 0.2, GL11.GL_QUADS);
								GL11.glPopMatrix();
							}
						}
					}
				}
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_LIGHTING);
				
				GL11.glDisable(GL11.GL_STENCIL_TEST);
				GL11.glPopMatrix();*/
			}

		}
	}

	private void renderBox(double x, double y, double z, int mode) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(mode);

		tessellator.setColorRGBA_F(1, 0.8F, 0.6F, 1);
		//if(alpha != 1.0)GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		tessellator.setNormal(0, 0, -1);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, 0, y);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, x, y);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, x, 0);

		tessellator.setNormal(0, 0, 1);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, x, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, x, y);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, 0, y);

		tessellator.setNormal(-1, 0, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0, z);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, y, z);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, y, 0);

		tessellator.setNormal(1, 0, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, y, 0);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, y, z);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, 0, z);

		tessellator.setNormal(0, 1, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(-x / 2, y / 2, z / 2, 0, z);
		tessellator.addVertexWithUV(x / 2, y / 2, z / 2, x, z);
		tessellator.addVertexWithUV(x / 2, y / 2, -z / 2, x, 0);

		tessellator.setNormal(0, -1, 0);
		tessellator.addVertexWithUV(-x / 2, -y / 2, -z / 2, 0, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, -z / 2, x, 0);
		tessellator.addVertexWithUV(x / 2, -y / 2, z / 2, x, z);
		tessellator.addVertexWithUV(-x / 2, -y / 2, z / 2, 0, z);

		tessellator.draw();

		//GL11.glDepthMask(true);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}

}
