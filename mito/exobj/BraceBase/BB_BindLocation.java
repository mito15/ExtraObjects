package com.mito.exobj.BraceBase;

public class BB_BindLocation {
	
	public int location;
	public int id;
	public ExtraObject base;
	
	public BB_BindLocation (int b, int l){
		this.location = l;
		this.id = b;
	}
	
	public BB_BindLocation (ExtraObject b, int l){
		this.location = l;
		this.base = b;
	}

}
