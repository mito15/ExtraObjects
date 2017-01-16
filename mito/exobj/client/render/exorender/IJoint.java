package com.mito.exobj.client.render.exorender;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.Brace.Brace;

public interface IJoint {
	
	public void drawBraceTessellator(Brace brace);

	public void drawBracewithVBO(CreateVertexBufferObject buffer, Brace brace);

}
