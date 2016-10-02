package com.mito.exobj.BraceBase.Brace.Render;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.VBOHandler;
import com.mito.exobj.BraceBase.VBOList;
import com.mito.exobj.BraceBase.Brace.Brace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class RenderBrace extends BB_Render {

	public void staticRender(ExtraObject base) {
		Tessellator t = Tessellator.instance;

		int i = base.getBrightnessForRender(0);
		int j = i % 65536;
		int k = i / 65536;
		Brace brace = (Brace) base;
		Minecraft.getMinecraft().renderEngine.bindTexture(brace.texture.getResourceLocation(brace.color));
		if (brace.shape == null)
			return;

		brace.shape.renderBraceAt(brace, 0);

	}

	@Override
	public void drawHighLight(ExtraObject base, float partialticks) {
		float size = 2.0F;
		GL11.glPushMatrix();
		if (base.isStatic) {
			GL11.glTranslated(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
		} else {
			double x = base.prevPos.xCoord + (base.pos.xCoord - base.prevPos.xCoord) * (double) partialticks;
			double y = base.prevPos.yCoord + (base.pos.yCoord - base.prevPos.yCoord) * (double) partialticks;
			double z = base.prevPos.zCoord + (base.pos.zCoord - base.prevPos.zCoord) * (double) partialticks;
			GL11.glTranslated(x, y, z);
		}
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);

		GL11.glLineWidth(size);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (base.buffer != null)
			base.buffer.draw(GL11.GL_LINE_LOOP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public void doRender(ExtraObject base, float partialTickTime) {
		BB_RenderHandler.enableClient();
		Brace brace = (Brace) base;
		Minecraft.getMinecraft().renderEngine.bindTexture(brace.texture.getResourceLocation(brace.color));
		GL11.glTranslated(brace.rand.xCoord, brace.rand.yCoord, brace.rand.zCoord);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		if (base.buffer != null) {
			base.buffer.draw();
		}
	}

	public void updateRender(ExtraObject base, float partialticks) {

		int i = base.getBrightnessForRender(partialticks);
		int j = i % 65536;
		int k = i / 65536;

		base.shouldUpdateRender = false;
		Brace brace = (Brace) base;
		if (brace.shape == null)
			return;
		base.buffer = new VBOList(new VBOHandler[0]);
		brace.shape.drawBrace(base.buffer, brace);
		CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
		c.beginRegist(35044, 7);
		c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		c.setBrightness(j, k);
		brace.shape.drawBraceSquare(c, brace);
		VBOHandler vbo1 = c.end();
		c.beginRegist(35044, 4);
		c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		c.setBrightness(j, k);
		brace.shape.drawBraceTriangle(c, brace);
		VBOHandler vbo2 = c.end();
		base.buffer.add(vbo1);
		base.buffer.add(vbo2);
	}
}
