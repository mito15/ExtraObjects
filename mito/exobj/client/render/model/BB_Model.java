package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.client.render.CreateVertexBufferObject;

import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class BB_Model implements IDrawable {

	public List<BB_Polygon> planes = new ArrayList<BB_Polygon>();

	public BB_Model(BB_Polygon... list) {
		for (BB_Polygon poly : list)
			planes.add(poly);
	}

	public void drawWithTessellator(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
	}

	public void drawWithVBO(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size) {
	}

	public void drawVBOIIcon(CreateVertexBufferObject c, IIcon iicon) {
		for(BB_Polygon p : this.planes){
			p.drawVBOIIcon(c, iicon);
		}
	}

}