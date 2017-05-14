package com.mito.exobj.client.render.model;

import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.utilities.MitoMath;

import net.minecraft.util.IIcon;

public class Triangle {

	public Vertex[] vertexs = new Vertex[3];

	public Triangle(Vertex v1, Vertex v2, Vertex v3) {
		this.vertexs[0] = v1;
		this.vertexs[1] = v2;
		this.vertexs[2] = v3;
	}

	/*public void draw(CreateVertexBufferObject c, double l) {
		c.setNormal(norm);
		c.registVertexWithUV(vertexs[0].addVector(0, 0, l));
		c.registVertexWithUV(vertexs[1].addVector(0, 0, l));
		c.registVertexWithUV(vertexs[2].addVector(0, 0, l));
	}
	
	public void drawReverse(CreateVertexBufferObject c, double l) {
		c.setNormal(MitoMath.vectorMul(norm, -1));
		c.registVertexWithUV(vertexs[2].addVector(0, 0, l));
		c.registVertexWithUV(vertexs[1].addVector(0, 0, l));
		c.registVertexWithUV(vertexs[0].addVector(0, 0, l));
	}
	
	public void draw(Tessellator c, double l) {
		c.setNormal((float) norm.xCoord, (float) norm.yCoord, (float) norm.zCoord);
		c.addVertexWithUV(vertexs[0].pos.xCoord, vertexs[0].pos.yCoord, vertexs[0].pos.zCoord + l, vertexs[0].u, vertexs[0].v);
		c.addVertexWithUV(vertexs[1].pos.xCoord, vertexs[1].pos.yCoord, vertexs[1].pos.zCoord + l, vertexs[1].u, vertexs[1].v);
		c.addVertexWithUV(vertexs[2].pos.xCoord, vertexs[2].pos.yCoord, vertexs[2].pos.zCoord + l, vertexs[2].u, vertexs[2].v);
	}
	
	public void drawReverse(Tessellator c, double l) {
		c.setNormal((float) -norm.xCoord, (float) -norm.yCoord, (float) -norm.zCoord);
		c.addVertexWithUV(vertexs[2].pos.xCoord, vertexs[2].pos.yCoord, vertexs[2].pos.zCoord + l, vertexs[2].u, vertexs[2].v);
		c.addVertexWithUV(vertexs[1].pos.xCoord, vertexs[1].pos.yCoord, vertexs[1].pos.zCoord + l, vertexs[1].u, vertexs[1].v);
		c.addVertexWithUV(vertexs[0].pos.xCoord, vertexs[0].pos.yCoord, vertexs[0].pos.zCoord + l, vertexs[0].u, vertexs[0].v);
	}
	
	public void draw(CreateVertexBufferObject c, Vec3 v, double roll, double pitch, double yaw) {
		c.setNormal(MitoMath.rot(norm, roll, pitch, yaw));
	
		c.registVertexWithUV(vertexs[0].rot(roll, pitch, yaw).addVector(v));
		c.registVertexWithUV(vertexs[1].rot(roll, pitch, yaw).addVector(v));
		c.registVertexWithUV(vertexs[2].rot(roll, pitch, yaw).addVector(v));
	}
	
	public void drawReverse(CreateVertexBufferObject c, Vec3 v, double roll, double pitch, double yaw) {
		c.setNormal(MitoMath.vectorMul(MitoMath.rot(norm, roll, pitch, yaw), -1));
	
		c.registVertexWithUV(vertexs[2].rot(roll, pitch, yaw).addVector(v));
		c.registVertexWithUV(vertexs[1].rot(roll, pitch, yaw).addVector(v));
		c.registVertexWithUV(vertexs[0].rot(roll, pitch, yaw).addVector(v));
	}*/

	public void drawIconR(CreateVertexBufferObject c, IIcon iicon) {
		if (iicon != null) {
			double mu = iicon.getMinU();
			double mv = iicon.getMinV();
			double du = iicon.getMaxU() - iicon.getMinU();
			double dv = iicon.getMaxV() - iicon.getMinV();
			double minu = Math.min(Math.min(vertexs[0].u, vertexs[1].u), vertexs[2].u);
			double minv = Math.min(Math.min(vertexs[0].v, vertexs[1].v), vertexs[2].v);
			double osu = Math.floor(minu + 0.0001);
			double osv = Math.floor(minv + 0.0001);
			c.setNormal(MitoMath.vectorMul(vertexs[2].norm, -1));
			c.registVertexWithUV(vertexs[2].pos, (vertexs[2].u - osu) * du + mu, mv + (vertexs[2].v - osv) * dv);
			c.setNormal(MitoMath.vectorMul(vertexs[1].norm, -1));
			c.registVertexWithUV(vertexs[1].pos, (vertexs[1].u - osu) * du + mu, mv + (vertexs[1].v - osv) * dv);
			c.setNormal(MitoMath.vectorMul(vertexs[0].norm, -1));
			c.registVertexWithUV(vertexs[0].pos, (vertexs[0].u - osu) * du + mu, mv + (vertexs[0].v - osv) * dv);
		}
	}

	public void drawIcon(CreateVertexBufferObject c, IIcon iicon) {
		if (iicon != null) {
			double mu = iicon.getMinU();
			double mv = iicon.getMinV();
			double du = iicon.getMaxU() - iicon.getMinU();
			double dv = iicon.getMaxV() - iicon.getMinV();
			double minu = Math.min(Math.min(vertexs[0].u, vertexs[1].u), vertexs[2].u);
			double minv = Math.min(Math.min(vertexs[0].v, vertexs[1].v), vertexs[2].v);
			double osu = Math.floor(minu + 0.0001);
			double osv = Math.floor(minv + 0.0001);
			c.setNormal(vertexs[0].norm);
			c.registVertexWithUV(vertexs[0].pos, (vertexs[0].u - osu) * du + mu, mv + (vertexs[0].v - osv) * dv);
			c.setNormal(vertexs[1].norm);
			c.registVertexWithUV(vertexs[1].pos, (vertexs[1].u - osu) * du + mu, mv + (vertexs[1].v - osv) * dv);
			c.setNormal(vertexs[2].norm);
			c.registVertexWithUV(vertexs[2].pos, (vertexs[2].u - osu) * du + mu, mv + (vertexs[2].v - osv) * dv);
		}
	}

}
