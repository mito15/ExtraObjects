package com.mito.exobj.client.render.model;

import com.mito.exobj.utilities.MitoMath;

import net.minecraft.util.Vec3;

public class D_Ellipse extends BB_Polygon {
	Vec3 pos;
	double axisX;
	double axisY;
	Vec3 normalRotation;

	public D_Ellipse(Vec3 p, Vec3 nr, double min, double maj) {
		this.pos = p;
		this.normalRotation = nr;
		this.axisX = min;
		this.axisY = maj;
	}

	public int getSize(double size) {
		return (size < 1.0) ? 20 : (int)(size * 20);
	}

	public Vec3 getVec3(int n, double size) {
		return Vec3.createVectorHelper(0.5 * size * axisX * Math.cos((double) n / (double) this.getSize(size) * 2 * Math.PI), 0.5 * size * axisX * Math.sin((double) n / (double) this.getSize(size) * 2 * Math.PI), 0);
	}

	private Vec3 getNorm1(Vec3 v1, Vec3 v2) {
		return MitoMath.unitVector(v1);
	}

	private Vec3 getNorm2(Vec3 v1, Vec3 v2) {
		return MitoMath.unitVector(v2);
	}

	public Vertex getVertex(int n, double size) {
		Vec3 vec3 = this.getVec3(n, size);
		return new Vertex(vec3, vec3.xCoord, vec3.yCoord);
	}
	
	public boolean hasNull() {
		for (int n = 0; n < this.line.size(); n++) {
			if (this.line.get(n) == null) {
				return true;
			}
		}
		return false;
	}
}
