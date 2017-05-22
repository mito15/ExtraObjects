package com.mito.exobj.client.render.model;

import com.mito.exobj.utilities.MitoMath;

import net.minecraft.util.Vec3;

public class Vertex {

	public final Vec3 pos;
	public final double u;
	public final double v;
	public final Vec3 norm;

	public Vertex(double x, double y) {
		this(x, y, 0);
	}

	public Vertex(double x, double y, double z) {
		this(x, y, z, x, y);
	}

	public Vertex(Vec3 p) {
		this(p, p.xCoord, p.yCoord);
	}

	public Vertex(Vec3 p, double u, double v) {
		this(p, u, v, Vec3.createVectorHelper(0, 0, 1));
	}

	public Vertex(double x, double y, double z, double u, double v) {
		this(Vec3.createVectorHelper(x, y, z), u, v);
	}

	public Vertex(double x, double y, double z, double u, double v, Vec3 norm) {
		this(Vec3.createVectorHelper(x, y, z), u, v, norm);
	}

	public Vertex(Vec3 v1, double u, double v, Vec3 norm) {
		this.pos = v1;
		this.u = u;
		this.v = v;
		this.norm = norm;
	}

	public Vertex resize(double size) {
		return new Vertex(MitoMath.vectorMul(this.pos, size), this.u * size + 0.5, this.v * size + 0.5);
	}

	public Vertex addVector(double i, double j, double l) {
		return new Vertex(this.pos.addVector(i, j, l), this.u + i, this.v + j);
	}

	public Vertex addVector(Vec3 v) {
		return this.addVector(v.xCoord, v.yCoord, v.zCoord);
	}

	public Vertex rot(double roll, double pitch, double yaw) {
		// TODO 自動生成されたメソッド・スタブ
		return new Vertex(MitoMath.rot(this.pos, roll, pitch, yaw), this.u, this.v);
	}

	public Vertex copy() {
		return new Vertex(pos, u, v, norm);
	}

	public Vertex transform(Mat4 mat) {
		return new Vertex(mat.transformVec3(pos), u, v, mat.transformVec3(norm));
	}

}
