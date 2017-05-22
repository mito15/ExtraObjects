package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class BB_Polygon implements IDrawBrace {

	public List<Vertex> line = new ArrayList();

	public BB_Polygon() {
	}

	public BB_Polygon(Vertex... list) {
		for (int n = 0; n < list.length; n++) {
			this.line.add(list[n]);
		}
	}

	public BB_Polygon(double... list) {
		for (int n = 0; n < list.length / 3; n++) {
			Vertex v = new Vertex(list[(3 * n)], list[(3 * n + 1)], list[(3 * n + 2)], 0.0D, 0.0D);
			this.line.add(v);
		}
	}

	public BB_Polygon(Triangle tri) {
		for (Vertex v : tri.vertexs) {
			line.add(v.copy());
		}
	}

	public int getSize(double size) {
		return this.line.size();
	}

	public Vec3 getVec3(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return MitoMath.vectorMul(((Vertex) this.line.get(n)).pos, size);
	}

	public Vertex getVertex(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return ((Vertex) this.line.get(n)).resize(size);
	}

	public void tessVertex(Tessellator t, Vertex v) {
		t.addVertexWithUV(v.pos.xCoord, v.pos.yCoord, v.pos.zCoord, v.u, v.v);
	}

	public boolean hasNull() {
		if (this.line.size() == 0) {
			return true;
		}
		for (int n = 0; n < this.line.size(); n++) {
			if (this.line.get(n) == null) {
				return true;
			}
		}
		return false;
	}
	
	public BB_Polygon transform(Mat4 mat) {
		for(Vertex v : this.line){
			v.transform(mat);
		}
		return this;
	}

	private BB_Polygon copy() {
		Vertex[] ret = new Vertex[this.line.size()];
		for(int n = 0; n < line.size(); n++){
			ret[n] = this.line.get(n).copy();
		}
		return new BB_Polygon(ret);
	}

	public BB_Model getModel(Brace brace) {
		double size = brace.size;
		double roll = brace.getRoll();
		BB_Model ret = new BB_Model();

		ILineBrace bc = brace.line;
		BB_Polygon p = this.resize(size);
				Mat4 mat = Mat4.createMat4();
				mat.addCoord(MitoMath.sub_vector(bc.getStart(), brace.pos));
				mat.matrixProduct(MyUtil.getRotationMatrix(bc.getTangent(0.0), bc.secondTan(0.0)));
				mat.rpyRotation(roll, 0, 0);
				ret.planes.add(p.copy().transform(mat));
				mat = Mat4.createMat4();
				mat.addCoord(MitoMath.sub_vector(bc.getEnd(), brace.pos));
				mat.matrixProduct(MyUtil.getRotationMatrix(bc.getTangent(1.0), bc.secondTan(1.0)));
				mat.rpyRotation(roll, 0, 0);
				ret.planes.add(p.copy().transform(mat));
		double v = 0.0D;
		LineWithDirection[] ls = bc.getDrawLine();
		for (LineWithDirection lwd : ls) {
			Vec3 s = MitoMath.sub_vector(lwd.list[0], brace.pos);
			Vec3 e = MitoMath.sub_vector(lwd.list[1], brace.pos);
			Mat4 ms = MyUtil.getRotationMatrix(lwd.list[2], lwd.list[4]);
			Mat4 me = MyUtil.getRotationMatrix(lwd.list[3], lwd.list[5]);
			double usum = 0.0D;
			double vofst = MitoMath.subAbs(s, e);
			for (int n1 = 0; n1 < getSize(size); n1++) {
				Vec3 v1 = getVec3(n1 - 1, size);
				Vec3 v2 = getVec3(n1, size);
				Vec3 vs1 = MitoMath.vectorSum(ms.transformNormal(MitoMath.rotZ(v1, roll)), s);
				Vec3 vs2 = MitoMath.vectorSum(ms.transformNormal(MitoMath.rotZ(v2, roll)), s);
				Vec3 ven1 = MitoMath.vectorSum(me.transformNormal(MitoMath.rotZ(v1, roll)), e);
				Vec3 ven2 = MitoMath.vectorSum(me.transformNormal(MitoMath.rotZ(v2, roll)), e);
				Vec3 norm = MitoMath.unitVector(Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D));
				Vec3 norm1 = ms.transformNormal(MitoMath.rotZ(norm, roll));
				Vec3 norm2 = me.transformNormal(MitoMath.rotZ(norm, roll));
				double uofst = MitoMath.subAbs(v1, v2);
				double zofst = v1.zCoord - v2.zCoord;
				Vertex ve1 = new Vertex(vs1, v, usum, norm1);
				Vertex ve2 = new Vertex(vs2, v + zofst, uofst + usum, norm1);
				Vertex ve3 = new Vertex(ven2, v + vofst + zofst, uofst + usum, norm2);
				Vertex ve4 = new Vertex(ven1, v + vofst, usum, norm2);
				BB_Polygon square = new BB_Polygon(ve1, ve2, ve3, ve4);
				ret.planes.add(square);
				usum += uofst;
			}
			v += vofst;
		}
		return ret;
	}

	private BB_Polygon resize(double size) {
		Vertex[] ret = new Vertex[getSize(size)];
		for (int n1 = 0; n1 < getSize(size); n1++) {
			ret[n1] = getVertex(n1, size);
		}
		return new BB_Polygon(ret);
	}

	@Override
	public void drawBracewithVBO(CreateVertexBufferObject c, Brace brace) {
		//double rofst = (brace.pos.xCoord + brace.pos.yCoord + brace.pos.zCoord) % 1.0D;
		IIcon iicon = brace.getIIcon(brace.color);
		double size = brace.size;
		double roll = brace.getRoll();

		ILineBrace bc = brace.line;
		c.pushMatrix();
		c.translate(brace.pos);
		BB_Model model = this.getModel(brace);
		for(BB_Polygon p : model.planes){
			List<Triangle> arrayTriangle = MyUtil.decomposeTexture(p);
			for (Triangle triangle : arrayTriangle) {
				triangle.drawIcon(c, iicon);
			}
		}
		c.popMatrix();
	}

	private List<Triangle> getTriangles(double size) {
		List<Vertex> ret = new ArrayList();
		for (int n1 = 0; n1 < getSize(size); n1++) {
			ret.add(getVertex(n1, size));
		}
		return MyUtil.decomposePolygon(ret);
	}

	public double maxU() {
		double maxu = Double.MIN_VALUE;
		for (Vertex v : line) {
			if (v.u > maxu) {
				maxu = v.u;
			}
		}
		return maxu;
	}

	public double maxV() {
		double maxv = Double.MIN_VALUE;
		for (Vertex v : line) {
			if (v.v > maxv) {
				maxv = v.v;
			}
		}
		return maxv;
	}

	public double minU() {
		double maxu = Double.MAX_VALUE;
		for (Vertex v : line) {
			if (v.u < maxu) {
				maxu = v.u;
			}
		}
		return maxu;
	}

	public double minV() {
		double maxu = Double.MAX_VALUE;
		for (Vertex v : line) {
			if (v.v < maxu) {
				maxu = v.v;
			}
		}
		return maxu;
	}

	@Override
	public void drawBraceTessellator(Brace brace, float partialTickTime) {
		// TODO Auto-generated method stub

	}

}
