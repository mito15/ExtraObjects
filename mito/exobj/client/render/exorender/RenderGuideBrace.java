package com.mito.exobj.client.render.exorender;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;

import net.minecraft.util.Vec3;

public class RenderGuideBrace extends BB_Render {
	
	public void doRender(ExtraObject base, float partialTickTime) {
		Brace brace = (Brace) base;
		Vec3 a = brace.line.getPoint(0.0);
		Vec3 b = brace.line.getPoint(1.0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(5.0F);
		GL11.glColor4f(0.8F, 0.8F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(a.xCoord, a.yCoord, a.zCoord);
		GL11.glVertex3d(b.xCoord, b.yCoord, b.zCoord);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
