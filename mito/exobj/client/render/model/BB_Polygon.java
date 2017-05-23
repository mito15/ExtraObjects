package com.mito.exobj.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.BraceBase.Brace.Brace;
import com.mito.exobj.client.render.CreateVertexBufferObject;
import com.mito.exobj.client.render.model.Triangle.EnumFace;
import com.mito.exobj.utilities.MitoMath;
import com.mito.exobj.utilities.MyUtil;

import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class BB_Polygon implements IDrawBrace, IDrawable {

	private List<Vertex> line = new ArrayList();
	public boolean smoothShading = false;

	public BB_Polygon() {
	}

	public BB_Polygon(Vertex... list) {
		for (int n = 0; n < list.length; n++) {
			this.getLine().add(list[n]);
		}
	}

	public BB_Polygon(List<Vertex> line) {
		this.line = line;
	}

	public BB_Polygon(double... list) {
		for (int n = 0; n < list.length / 3; n++) {
			Vertex v = new Vertex(list[(3 * n)], list[(3 * n + 1)], list[(3 * n + 2)], 0.0D, 0.0D);
			this.getLine().add(v);
		}
	}

	public BB_Polygon(Triangle tri) {
		for (Vertex v : tri.vertexs) {
			getLine().add(v.copy());
		}
	}/*

	public int getSize(double size) {
		return this.getLine().size();
	}

	public Vec3 getVec3(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return MitoMath.vectorMul(((Vertex) this.getLine().get(n)).pos, size);
	}

	public Vertex getVertex(int n, double size) {
		if ((n < 0) || (n >= getSize(size))) {
			n %= getSize(size);
			if (n < 0) {
				n += getSize(size);
			}
		}
		return ((Vertex) this.getLine().get(n)).resize(size);
	}*/

	public BB_Polygon transform(Mat4 mat) {
		Vertex[] ret = new Vertex[this.getLine().size()];
		for (int n = 0; n < getLine().size(); n++) {
			ret[n] = this.getLine().get(n).transform(mat);
		}
		return new BB_Polygon(ret);
	}

	private BB_Polygon copy() {
		Vertex[] ret = new Vertex[this.getLine().size()];
		for (int n = 0; n < getLine().size(); n++) {
			ret[n] = this.getLine().get(n).copy();
		}
		return new BB_Polygon(ret);
	}

	public BB_Model getModel(Brace brace) {
		double size = brace.size;
		double roll = brace.getRoll();
		BB_Model ret = new BB_Model();

		ILineBrace bc = brace.line;
		LineWithDirection[] ls = bc.getDrawLine();
		if (ls.length < 1) {
			return ret;
		}
		BB_Polygon p = this.resize(size);
		Mat4 mat = Mat4.createMat4();
		mat.addCoord(MitoMath.sub_vector(bc.getStart(), brace.pos));
		mat.transMat(ls[0].mat1);
		mat.rpyRotation(roll, 0, 0);
		ret.planes.add(p.reverse().transform(mat));
		mat = Mat4.createMat4();
		mat.addCoord(MitoMath.sub_vector(bc.getEnd(), brace.pos));
		mat.transMat(ls[ls.length - 1].mat2);
		mat.rpyRotation(roll, 0, 0);
		ret.planes.add(p.transform(mat));
		double v = 0.0D;
		List<Vec3> line = getLineWithRoll(roll);
		for (LineWithDirection lwd : ls) {
			Vec3 s = MitoMath.sub_vector(lwd.start, brace.pos);
			Vec3 e = MitoMath.sub_vector(lwd.end, brace.pos);
			double usum = 0.0D;
			double vofst = MitoMath.subAbs(s, e);
			for (int n1 = 0; n1 < line.size(); n1++) {
				Vec3 v1 = line.get(n1 == 0 ? line.size() - 1 : n1 - 1);
				Vec3 v2 = line.get(n1);
				Vec3 vs1 = MitoMath.vectorSum(lwd.mat1.transformNormal(MitoMath.rotZ(v1, roll)), s);
				Vec3 vs2 = MitoMath.vectorSum(lwd.mat1.transformNormal(MitoMath.rotZ(v2, roll)), s);
				Vec3 ven1 = MitoMath.vectorSum(lwd.mat2.transformNormal(MitoMath.rotZ(v1, roll)), e);
				Vec3 ven2 = MitoMath.vectorSum(lwd.mat2.transformNormal(MitoMath.rotZ(v2, roll)), e);
				Vec3 norm = MitoMath.unitVector(Vec3.createVectorHelper(v2.yCoord - v1.yCoord, v1.xCoord - v2.xCoord, 0.0D));
				Vec3 norm1 = lwd.mat1.transformNormal(MitoMath.rotZ(norm, roll));
				Vec3 norm2 = lwd.mat2.transformNormal(MitoMath.rotZ(norm, roll));
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

	private List<Vec3> getLineWithRoll(double roll) {
		List<Vec3> ret = new ArrayList<Vec3>();
		List<Vertex> line = getLine();
		for(Vertex v : line){
			ret.add(MitoMath.rotZ(v.pos, roll));
		}
		return ret;
	}

	private BB_Polygon reverse() {
		Vertex[] ret = new Vertex[this.getLine().size()];
		int nmax = getLine().size();
		for (int n = 0; n < nmax; n++) {
			ret[nmax - n - 1] = this.getLine().get(n).copy();
		}
		return new BB_Polygon(ret);
	}

	private BB_Polygon resize(double size) {
		List<Vertex> ret = getLine();
		return new BB_Polygon(ret);
	}

	private List<Triangle> getTriangles(double size) {
		List<Vertex> ret = getLine();
		return MyUtil.decomposePolygon(ret);
	}

	public List<Vertex> getLine() {
		return getLine(1.0);
	}
	
	public List<Vertex> getLine(double size) {
		return line;
	}

	@Override
	public void drawWithTessellator(Vec3 offset, double roll, double pitch, double yaw, double size, float partialTickTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawWithVBO(CreateVertexBufferObject c, Vec3 offset, double roll, double pitch, double yaw, double size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawVBOIIcon(CreateVertexBufferObject c, IIcon iicon) {
		List<Triangle> arrayTriangle = MyUtil.decomposeTexture(this);
		for (Triangle triangle : arrayTriangle) {
			triangle.drawIcon(c, iicon, EnumFace.OBVERSE);
		}
	}

}
