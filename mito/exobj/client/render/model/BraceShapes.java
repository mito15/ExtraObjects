package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.VBOList;
import com.mito.exobj.BraceBase.Brace.Brace;

public class BraceShapes implements IDrawBrace {

	public IDrawBrace[] planes;


	public BraceShapes(IDrawBrace... list) {
		planes = list;
	}

	public void drawBrace(VBOList vbolist, Brace brace) {
		for (int n = 0; n < this.planes.length; n++) {
			IDrawBrace plane = this.planes[n];
			if (plane != null)
				plane.drawBrace(vbolist, brace);
		}
	}

	public void drawBraceSquare(CreateVertexBufferObject c, Brace brace) {
		for (int n = 0; n < this.planes.length; n++) {
			IDrawBrace plane = this.planes[n];
			if (plane != null)
				plane.drawBraceSquare(c, brace);
		}
	}

	public void drawBraceTriangle(CreateVertexBufferObject c, Brace brace) {
		for (int n = 0; n < this.planes.length; n++) {
			IDrawBrace plane = this.planes[n];
			if (plane != null)
				plane.drawBraceTriangle(c, brace);
		}
	}

	@Override
	public void renderBraceAt(Brace brace, float partialTickTime) {
		for (int n = 0; n < this.planes.length; n++) {
			IDrawBrace plane = this.planes[n];
			if (plane != null)
				plane.renderBraceAt(brace, partialTickTime);;
		}
	}

	@Override
	public boolean hasNull() {
		return false;
	}

}
