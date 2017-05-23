package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.Brace.Brace;

public class BraceShapes implements IDrawBrace {

	public IDrawBrace[] planes;


	public BraceShapes(IDrawBrace... list) {
		planes = list;
	}

	@Override
	public BB_Model getModel(Brace brace) {
		for (IDrawBrace plane : planes) {
			if (plane != null)
				plane.getModel(brace);
		}
		return null;
	}

}
