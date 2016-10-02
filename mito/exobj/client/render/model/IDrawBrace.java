package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.VBOList;
import com.mito.exobj.BraceBase.Brace.Brace;

public interface IDrawBrace {

	public void renderBraceAt(Brace brace, float partialTickTime);

	public void drawBrace(VBOList buffer, Brace brace);

	public void drawBraceSquare(CreateVertexBufferObject c, Brace brace);

	public void drawBraceTriangle(CreateVertexBufferObject buffer, Brace brace);

}
