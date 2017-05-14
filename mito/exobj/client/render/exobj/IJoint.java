package com.mito.exobj.client.render.exobj;

import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.CreateVertexBufferObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IJoint {

	@SideOnly(Side.CLIENT)
	public void drawJointTessellator(Brace brace);

	@SideOnly(Side.CLIENT)
	public void drawJointwithVBO(CreateVertexBufferObject buffer, Brace brace);

}
