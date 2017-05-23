package com.mito.exobj.client.render.exobj;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Brace;

import net.minecraft.util.Vec3;

public class RenderGuideBrace extends BB_Render {

	@Override
	public void doRender(ExtraObject base, float x, float y, float z, float partialTickTime) {
		Brace brace = (Brace) base;
		Vec3 a = brace.line.getStart();
		Vec3 b = brace.line.getEnd();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(5.0F);
		GL11.glColor4f(0.8F, 0.8F, 1.0F, 1.0F);
		GL11.glBegin(1);

		GL11.glVertex3d(a.xCoord + x, a.yCoord + y, a.zCoord + z);
		GL11.glVertex3d(b.xCoord + x, b.yCoord + y, b.zCoord + z);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
