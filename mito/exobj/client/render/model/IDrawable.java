package com.mito.exobj.client.render.model;

import com.mito.exobj.client.render.CreateVertexBufferObject;

import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public interface IDrawable {
	
	public void drawWithTessellator(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime);

	public void drawWithVBO(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size);

	public void drawVBOIIcon(CreateVertexBufferObject c, IIcon iicon);

}
