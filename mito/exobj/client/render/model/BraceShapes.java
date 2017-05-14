package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.CreateVertexBufferObject;

public class BraceShapes implements IDrawBrace {

	public IDrawBrace[] planes;


	public BraceShapes(IDrawBrace... list) {
		planes = list;
	}

	public void drawBracewithVBO(CreateVertexBufferObject c, Brace brace) {
		for (IDrawBrace plane : planes) {
			if (plane != null)
				plane.drawBracewithVBO(c, brace);
		}
	}

	@Override
	public void drawBraceTessellator(Brace brace, float partialTickTime) {
		for (int n = 0; n < this.planes.length; n++) {
			IDrawBrace plane = this.planes[n];
			if (plane != null)
				plane.drawBraceTessellator(brace, partialTickTime);;
		}
	}

	@Override
	public boolean hasNull() {
		return false;
	}

}
