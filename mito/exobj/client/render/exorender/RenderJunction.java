package com.mito.exobj.client.render.exorender;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Junction;
import com.mito.exobj.client.render.RenderHighLight;

public class RenderJunction extends BB_Render {

	public void doRender(ExtraObject base, float pt) {
		if (base != null && base instanceof Junction) {
			RenderHighLight rh = RenderHighLight.INSTANCE;
			GL11.glPushMatrix();
			
			
			GL11.glPopMatrix();
		}
	}

}
