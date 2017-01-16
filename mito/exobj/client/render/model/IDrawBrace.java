package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.Brace.Brace;

public interface IDrawBrace {

	public void drawBraceTessellator(Brace brace, float partialTickTime);

	public void drawBracewithVBO(CreateVertexBufferObject buffer, Brace brace);

	public boolean hasNull();

}
