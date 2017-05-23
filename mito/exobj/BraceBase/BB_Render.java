package com.mito.exobj.BraceBase;

import com.mito.exobj.client.render.CreateVertexBufferObject;

public abstract class BB_Render {

	public void drawHighLight(ExtraObject base, float pt){}


	public void doRender(ExtraObject base, float x, float y, float z, float partialticks){
		}
	
	public void updateRender(CreateVertexBufferObject c, ExtraObject base) {}


	public void updateRender(CreateVertexBufferObject c, ExtraObject base, double x, double y, double z) {
	}


	public boolean isVbo(ExtraObject base) {
		return false;
	}


}
