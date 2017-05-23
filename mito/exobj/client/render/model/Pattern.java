package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.Brace.Brace;

public class Pattern implements IDrawBrace {

	public IDrawable model;
	public double length;

	public Pattern(double length, IDrawable model) {
		this.model = model;
		this.length = length;
	}

	@Override
	public BB_Model getModel(Brace brace) {
		return null;
	}

}
