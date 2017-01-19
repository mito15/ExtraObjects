package com.mito.exobj.client.render.exorender;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;

public class RenderBrace extends BB_Render {

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
		/*if (base.buffer != null)
			base.buffer.draw(GL11.GL_LINE_LOOP);*/
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	}

	public void doRender(ExtraObject base, float partialTickTime) {
		//BB_RenderHandler.enableClient();
		Brace brace = (Brace) base;
		GL11.glTranslated(brace.rand.xCoord, brace.rand.yCoord, brace.rand.zCoord);
		//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		brace.shape.drawBraceTessellator(brace, partialTickTime);
		/*if (base.buffer != null) {
			base.buffer.draw();
		}*/
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base, float partialticks) {

		int i = base.getBrightnessForRender(partialticks);
		int j = i % 65536;
		int k = i / 65536;

		Brace brace = (Brace) base;
		if (brace.shape == null)
			return;
		c.setBrightness(j, k);
		c.pushMatrix();
		brace.shape.drawBracewithVBO(c, brace);
		c.popMatrix();
	}
}
