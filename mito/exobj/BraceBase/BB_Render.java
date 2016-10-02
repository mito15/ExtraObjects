package com.mito.exobj.BraceBase;

public abstract class BB_Render {

	public void drawHighLight(ExtraObject base, float pt){}


	public void doRender(ExtraObject base, float pt){
		}

	public void updateRender(ExtraObject base, float pt){
		base.shouldUpdateRender = false;
		}

	public void staticRender(ExtraObject base) {
	}
}
