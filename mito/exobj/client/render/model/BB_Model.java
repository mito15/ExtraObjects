package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;

import net.minecraft.util.Vec3;

public class BB_Model {

	public BB_Polygon[] planes;

	public BB_Model(BB_Polygon... list) {
		planes = list;
	}

	public void drawWithTessellator(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		for(int i = 0; i < planes.length; i++){
			planes[i].renderAt(offset, roll, pitch, yaw, size, partialTickTime);
		}
	}

	public void drawWithVBO(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size) {
		for(int i = 0; i < planes.length; i++){
			planes[i].drawPlane(c, offset, Vec3.createVectorHelper(1, 0, 0), roll, pitch, yaw, size);
		}
	}

}