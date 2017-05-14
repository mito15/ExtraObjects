package com.mito.exobj.client.render.exobj;

import org.lwjgl.opengl.GL11;

import com.mito.exobj.BraceBase.BB_Render;
import com.mito.exobj.BraceBase.ExtraObject;
import com.mito.exobj.BraceBase.Brace.Junction;
import com.mito.exobj.client.render.RenderHighLight;

public class RenderWall extends BB_Render {

	public void doRender(ExtraObject base, float x, float y, float z, float pt) {
		if (base != null && base instanceof Junction) {
			RenderHighLight rh = RenderHighLight.INSTANCE;
			GL11.glPushMatrix();
			
			
			GL11.glPopMatrix();
		}
	}

}
