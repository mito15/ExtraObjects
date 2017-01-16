package com.mito.exobj.client.render.exorender;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.FakeBlock;
import com.mito.exobj.client.render.RenderHighLight;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;

public class RenderFakeBlock extends BB_Render {

	public void doRender(ExtraObject base, float pt) {
		if (base != null && base instanceof FakeBlock) {
			FakeBlock fake = (FakeBlock) base;
			Block block = fake.contain;
			if (block != null && fake != null && fake.renderblocks != null) {
				BB_RenderHandler.disableClient();
				GL11.glTranslated(-0.5, -0.5, -0.5);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				RenderHelper.disableStandardItemLighting();
				Tessellator.instance.startDrawingQuads();
				fake.renderblocks.renderBlockByRenderType(block, 0, 0, 0);
				Tessellator.instance.draw();
				RenderHelper.enableStandardItemLighting();
			}
		}
	}

	public void drawHighLight(ExtraObject base, float partialticks) {
		GL11.glPushMatrix();
		if (base.isStatic) {
			GL11.glTranslated(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
		} else {
			double x = base.prevPos.xCoord + (base.pos.xCoord - base.prevPos.xCoord) * (double) partialticks;
			double y = base.prevPos.yCoord + (base.pos.yCoord - base.prevPos.yCoord) * (double) partialticks;
			double z = base.prevPos.zCoord + (base.pos.zCoord - base.prevPos.zCoord) * (double) partialticks;
			GL11.glTranslated(x, y, z);
		}

		GL11.glLineWidth(1.0F);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);

		RenderHighLight rh = RenderHighLight.INSTANCE;
		rh.renderBox(1.0, false);
		GL11.glPopMatrix();
	}

	private void renderBox(double x, double y, double z) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();

		tessellator.setColorRGBA_F(1, 1, 1, 1);
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
