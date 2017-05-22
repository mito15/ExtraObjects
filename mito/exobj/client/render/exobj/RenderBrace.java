package com.mito.exobj.client.render.exobj;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.mito.exobj.Main;
import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.BB_RenderHandler;
import com.mito.exobj.BraceBase.BB_ResisteredList;
import com.mito.exobj.BraceBase.BB_TypeResister;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.BB_HighLightHandler;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.VBOHandler;
import com.mito.exobj.client.render.model.IDrawBrace;
import com.mito.exobj.main.mitoClientProxy;

public class RenderBrace extends BB_Render {

	@Override
	public void drawHighLight(ExtraObject base, float partialticks) {
		float size = 2.0F;
		Brace brace = (Brace) base;
		if (brace.shape == null)
			return;

		BB_HighLightHandler data = ((mitoClientProxy) Main.proxy).bh;
		if (data.key == null || !data.key.equals(base)) {
			data.buffer.delete();
			CreateVertexBufferObject c = CreateVertexBufferObject.INSTANCE;
			c.beginRegist(GL15.GL_STATIC_DRAW, GL11.GL_TRIANGLES);
			c.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			BB_Render render = BB_ResisteredList.getBraceBaseRender(brace);
			render.updateRender(c, brace);
			data.key = base;
			VBOHandler vbo = c.end();
			data.buffer.add(vbo);
		}

		GL11.glPushMatrix();

		GL11.glLineWidth(size);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		BB_RenderHandler.enableClient();
		data.buffer.draw(GL11.GL_LINE_LOOP);
		BB_RenderHandler.disableClient();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		Brace brace = (Brace) base;
		GL11.glTranslated(brace.rand.xCoord, brace.rand.yCoord, brace.rand.zCoord);
		BB_TypeResister.getFigure(brace.shape).drawBraceTessellator(brace, partialTickTime);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base) {
		updateRender(c, base, 0, 0, 0);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base, double x, double y, double z) {
		int i = base.getBrightnessForRender(0, x, y, z);

		Brace brace = (Brace) base;
		if (brace.shape == null)
			return;
		c.setBrightness(i);
		int j = brace.texture.getRenderColor(brace.color);
		float f = (float)(j >> 16 & 255) / 255.0F;
		float g = (float)(j >> 8 & 255) / 255.0F;
		float h = (float)(j & 255) / 255.0F;;
		c.setColor(f, g, h);
		c.pushMatrix();
		IDrawBrace id = BB_TypeResister.getFigure(brace.shape);
		if (id != null)
			id.drawBracewithVBO(c, brace);
		IJoint ij = BB_TypeResister.getJoint(brace.joint);
		if (ij != null)
			ij.drawJointwithVBO(c, brace);
		c.popMatrix();

	}
	
	public boolean isVbo(ExtraObject base) {
		return base.isStatic;
	}
}
