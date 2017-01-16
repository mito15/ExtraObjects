package com.mito.exobj.client.render.exorender;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.client.render.RenderHighLight;

public class RenderScale extends BB_Render {

	public void doRender(ExtraObject base, float pt) {
		RenderHighLight rh = RenderHighLight.INSTANCE;
		GL11.glPushMatrix();
		//GL11.glTranslated(base.pos.xCoord, base.pos.yCoord, base.pos.zCoord);
		rh.renderLine(0.0, 0.0, 0.1);
		rh.renderLine(0.0, 0.0, -0.1);
		rh.renderLine(0.0, 0.1, 0.0);
		rh.renderLine(0.0, -0.1, 0.0);
		rh.renderLine(0.1, 0.0, 0.0);
		rh.renderLine(-0.1, 0.0, 0.0);
		GL11.glPopMatrix();

	}

}
