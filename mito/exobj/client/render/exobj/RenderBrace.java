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
import com.mito.exobj.client.render.model.BB_Model;
import com.mito.exobj.client.render.model.IDrawBrace;
import com.mito.exobj.main.mitoClientProxy;

import net.minecraft.util.IIcon;

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

		BB_RenderHandler.enableClient();
		data.buffer.draw(GL11.GL_LINE_LOOP);
		BB_RenderHandler.disableClient();

	}

	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		Brace brace = (Brace) base;
		GL11.glTranslated(brace.rand.xCoord, brace.rand.yCoord, brace.rand.zCoord);
		//BB_TypeResister.getFigure(brace.shape).drawBraceTessellator(brace, partialTickTime);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base) {
		updateRender(c, base, 0, 0, 0);
	}

	public void updateRender(CreateVertexBufferObject c, ExtraObject base, double x, double y, double z) {
		int i = base.getBrightnessForRender(0, x, y, z);

		Brace brace = (Brace) base;
		c.setBrightness(i);
		int j = brace.texture.getRenderColor(brace.color);
		c.setColor(j);
		c.pushMatrix();
		IDrawBrace id = BB_TypeResister.getFigure(brace.shape);
		if (id != null){
			IIcon iicon = brace.getIIcon(brace.color);
			c.pushMatrix();
			c.translate(brace.pos);
			BB_Model model = id.getModel(brace);
			model.drawVBOIIcon(c, iicon);
			
			c.popMatrix();
		}
		IJoint ij = BB_TypeResister.getJoint(brace.joint);
		if (ij != null)
			ij.drawJointwithVBO(c, brace);
		c.popMatrix();

	}
	
	public boolean isVbo(ExtraObject base) {
		return base.isStatic;
	}
}
