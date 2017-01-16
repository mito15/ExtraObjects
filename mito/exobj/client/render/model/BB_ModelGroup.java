package com.mito.exobj.client.render.model;

import com.mito.exobj.BraceBase.CreateVertexBufferObject;

import net.minecraft.util.Vec3;

public class BB_ModelGroup extends BB_Model {
	
	public BB_Model[] models;

	public BB_ModelGroup(BB_Model... list) {
		models = list;
	}

	public void drawWithTessellator(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		for(int i = 0; i < models.length; i++){
			models[i].drawWithTessellator(offset, roll, pitch, yaw, size, partialTickTime);
		}
	}

	public void drawWithVBO(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size) {
		for(int i = 0; i < models.length; i++){
			models[i].drawWithVBO(c, offset, roll, pitch, yaw, size);
		}
	}

}
