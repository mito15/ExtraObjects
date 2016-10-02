package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;
import com.mito.exobj.BraceBase.VBOList;

import net.minecraft.util.Vec3;

public interface IDrawable {

	public void renderAt(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime);

	public void drawQuad(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size);

	public void drawTri(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size);

	public void drawSpecial(VBOList c, Vec3 offset, double roll, double pitch, double yaw, double size);

}
